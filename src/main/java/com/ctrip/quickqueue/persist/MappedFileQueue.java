package com.ctrip.quickqueue.persist;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.intf.IPersistent;

import sun.nio.ch.DirectBuffer;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static java.nio.channels.FileChannel.MapMode.READ_WRITE;

/**
 * This is a memory mapped persistent queue where the objects are produced and consumed in a FIFO basis
 * <p/>
 * for persistent queue, two memory mapped byte buffers will be created both for reading and writing
 * <p/>
 * The data packet format will be [1byte][4bytes][data bytes] where first header is to mention whether
 * the packet is already consumed or not. second header is 4 bytes size, specifying the length of the packet,
 * and the rest is the actual data.
 * <p/>
 * The backlog is also taken care.
 * <p/>
 * The index file is used for keeping read and write file pointers.
 *
 * @param <T>
 * @author yjfei
 */

public class MappedFileQueue<T extends Serializable> implements IPersistent<T> {

    private final static Logger logger = LoggerFactory.getLogger(MappedFileQueue.class);

    private static final int PAGE_SIZE_MB = 128;
    private static final int PAGE_SIZE = 1024 * 1024 * PAGE_SIZE_MB; // 128 MB Page Size
    private static final int OBJECT_SIZE_LIMIT = 1024 * 1024 * 32; // 32 MB Object Size limit

    private static final int FILE_CLEAN_INTERVAL_SECONDS = 20;

    private RandomAccessFile readDataFile; //random access file for data
    private RandomAccessFile writeDataFile; //random access file for data
    private RandomAccessFile indexFile; // random access file for read/write position

    private FileChannel readDataChannel; // channel for read data
    private FileChannel writeDataChannel; // channel for write data
    private FileChannel indexChannel; // channel for read/write file index

    private MappedByteBuffer readMbb; // buffer used to read;
    private MappedByteBuffer writeMbb; // buffer used to write

    private MappedByteBuffer indexMbb; // buffer used for read/write position

    private boolean backlogAvailable = false; // If the persistent file has backlog already

    final private int headerLength = 5;
    final private byte READ = (byte) 0;
    final private byte NOT_READ = (byte) 1;
    final private byte MM_EOF = (byte) 2;

    final private ByteBuffer header = ByteBuffer.allocate(headerLength); // 1 byte for status of the message, 4 bytes length of the payload
    final private int endingLength = 5;

    private int readFileIndex = 0; // read index file
    private int writeFileIndex = 0; // write index file

    private final static int SIZE_OF_INT = 4;

    // remove old used files
    private Timer fileCleanTimer;

    private long maxFileSize;

    private static final String DATA_FILE_SUFFIX = ".dat";
    private static final String INDEX_FILE_SUFFIX = ".idx";


    private String fileName; // persistence file name
    private File queueDir; // queue directory

    /**
     * A memory mapped persistence queue.
     *
     * @param dir,         directory for queue data
     * @param qName,       the name of the persistent queue
     * @param maxFileSize, persistent file max size in MB,
     *                     must be positive and a multiple of 128MB.
     * @throws java.io.IOException
     */
    public MappedFileQueue(String dir, String qName, int maxFileSize) throws IOException {
        if (dir == null || dir.trim().length() == 0) {
            throw new IllegalArgumentException("dir is empty");
        }
        if (qName == null || qName.trim().length() == 0) {
            throw new IllegalArgumentException("name is empty");
        }
        String qDir = dir;
        if (!qDir.endsWith("/")) {
            qDir += File.separator;
        }
        qDir += qName;
        queueDir = new File(qDir);
        if (!queueDir.exists()) {
            queueDir.mkdirs();
        }
        this.fileName = queueDir.getPath();
        if (!this.fileName.endsWith("/")) {
            this.fileName += File.separator;
        }
        this.fileName += "data";
        if (maxFileSize < 0 || maxFileSize % PAGE_SIZE_MB != 0) {
            throw new IllegalArgumentException("max file size must be positive and a multiple of " + PAGE_SIZE_MB + " MB");
        }
        this.maxFileSize = maxFileSize;
        this.init();
    }

    private void init() throws IOException {
        this.initIndex();

        this.initReadBuffer();
        this.initWriteBuffer();

        fileCleanTimer = new Timer();
        fileCleanTimer.schedule(new FileCleanTask(), 1000,
                FILE_CLEAN_INTERVAL_SECONDS * 1000);
    }

    private void initIndex() throws IOException {
        indexFile = new RandomAccessFile(this.fileName + INDEX_FILE_SUFFIX, "rw");
        indexChannel = indexFile.getChannel();
        indexMbb = indexChannel.map(READ_WRITE, 0, SIZE_OF_INT * 2);
        readFileIndex = indexMbb.getInt();
        writeFileIndex = indexMbb.getInt();
        indexMbb.position(0);
    }

    private void initReadBuffer() throws IOException {
        readDataFile = new RandomAccessFile(this.fileName + "-" + readFileIndex + DATA_FILE_SUFFIX, "rw");
        readDataChannel = readDataFile.getChannel();
        readMbb = readDataChannel.map(READ_WRITE, 0, PAGE_SIZE); // create the read buffer with readPosition 0 initially
        int position = readMbb.position();
        byte active = readMbb.get(); // first byte to see whether the message is already read or not
        int length = readMbb.getInt(); // next four bytes to see the data length

        while (active == READ && length > 0) { // message is non active means, its read, so skipping it
            readMbb.position(position + headerLength + length); // skipping the read bytes
            position = readMbb.position();
            active = readMbb.get();
            length = readMbb.getInt();
        }
        if (active == NOT_READ) {
            logger.info("init read buffer, backlog is available in persistent queue");
            backlogAvailable = true; // the file has unconsumed message(s)
        }
        readMbb.position(position);

    }

    private void initWriteBuffer() throws IOException {
        writeDataFile = new RandomAccessFile(this.fileName + "-" + writeFileIndex + DATA_FILE_SUFFIX, "rw");
        writeDataChannel = writeDataFile.getChannel();
        writeMbb = writeDataChannel.map(READ_WRITE, 0, PAGE_SIZE); // start the write buffer with writePosition 0 initially
        int position = writeMbb.position();
        byte active = writeMbb.get();
        int length = writeMbb.getInt();
        while (length > 0) { // message is there, so skip it, keep doing until u get the end
            writeMbb.position(position + headerLength + length);
            position = writeMbb.position();
            active = writeMbb.get();
            length = writeMbb.getInt();
        }
        writeMbb.position(position);
    }

    @Override
    public T consume() {
        return this.consumeFromDiskFile();
    }

    @Override
    public boolean produce(T t) {
        return this.produceToDiskFile(t);
    }


    private synchronized T consumeFromDiskFile() {
        try {
//            if (readFileIndex == writeFileIndex && readMbb.position() == writeMbb.position()) {
//                // read and write accessing the same file
//                return null;
//            }
            int currentPosition = readMbb.position();
            byte active = readMbb.get();
            int length;
            // end of the file
            if (active == MM_EOF) {
                readMbb.force();
                unMap(readMbb);
                closeResource(readDataChannel);
                closeResource(readDataFile);

                if (readFileIndex == Integer.MAX_VALUE) {
                    readFileIndex = 0;
                } else {
                    readFileIndex += 1;
                }
                readDataFile = new RandomAccessFile(this.fileName + "-" + readFileIndex + DATA_FILE_SUFFIX, "rw");
                readDataChannel = readDataFile.getChannel();
                readMbb = readDataChannel.map(READ_WRITE, 0, PAGE_SIZE);
                indexMbb.putInt(0, readFileIndex); // update read file index
                indexMbb.force();
                currentPosition = readMbb.position();
                active = readMbb.get();
                length = readMbb.getInt();
            } else {
                length = readMbb.getInt();
            }

            if (length <= 0) {
                readMbb.position(currentPosition);
                return null; // the queue is empty
            }
            byte[] bytes = new byte[length];
            readMbb.get(bytes);

            readMbb.put(currentPosition, READ); // making it not active (deleted)

            return (T) toObject(bytes);
        } catch (Throwable e) {
            logger.error("Issue in reading the persistent queue : ", e);
            return null;
        }
    }

    private synchronized boolean produceToDiskFile(T t) {
        try {
            int backFileSize = this.getBackFileSize();
            if (backFileSize > maxFileSize) {
                logger.warn("Issue in dumping the object into persistent, disk file size " + backFileSize +
                        " MB exceeds max limit " + maxFileSize + "MB");
                return false;
            }

            byte[] oBytes = getBytes(t);
            int length = oBytes.length;
            if (length == 0) {
                logger.warn("Issue in dumping the object with zero byte into persistent queue");
                return false;
            }
            if (length > OBJECT_SIZE_LIMIT) {
                logger.warn("Issue in dumping the object into persistent queue, object size " + length +
                        " exceeds limit " + OBJECT_SIZE_LIMIT + " MB");
                return false;
            }

            //prepare the header
            header.clear();
            header.put(NOT_READ);
            header.putInt(length);
            header.flip();

            if (writeMbb.remaining() < headerLength + length + endingLength) { // check weather current buffer is enuf, otherwise we need to change the buffer
                writeMbb.put(MM_EOF); // the end
                writeMbb.force();
                unMap(writeMbb);
                closeResource(writeDataChannel);
                closeResource(writeDataFile);

                if (writeFileIndex == Integer.MAX_VALUE) {
                    writeFileIndex = 0;
                } else {
                    writeFileIndex += 1;
                }
                writeDataFile = new RandomAccessFile(this.fileName + "-" + writeFileIndex + DATA_FILE_SUFFIX, "rw");
                writeDataChannel = writeDataFile.getChannel();
                writeMbb = writeDataChannel.map(READ_WRITE, 0, PAGE_SIZE); // start the write buffer with writePosition 0 initially
                indexMbb.putInt(SIZE_OF_INT, writeFileIndex); // update write file index
            }

            writeMbb.put(header); // write header
            writeMbb.put(oBytes);

            return true;
        } catch (Throwable e) {
            logger.error("Issue in dumping the object into persistent " + e);
            //logger.error("The object missed is : " + t);
            return false;
        }
    }


    private static void unMap(MappedByteBuffer buffer) {
        if (buffer == null) return;
        sun.misc.Cleaner cleaner = ((DirectBuffer) buffer).cleaner();
        if (cleaner != null) {
            cleaner.clean();
        }
    }

    private static byte[] getBytes(Object o) throws IOException {
        ByteArrayOutputStream bos = null;
        ObjectOutputStream oos = null;
        try {
            bos = new ByteArrayOutputStream();
            // TODO: improvable by using a SeDe framewrok.
            oos = new ObjectOutputStream(bos);
            oos.writeObject(o);
            oos.flush();
            return bos.toByteArray();
        } finally {
            closeResource(bos);
            closeResource(oos);
        }
    }

    private static <T> T toObject(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        try {
            bis = new ByteArrayInputStream(bytes);
            ois = new ObjectInputStream(bis);
            return (T) ois.readObject();
        } finally {
            closeResource(bis);
            closeResource(ois);
        }
    }

    private static void closeResource(Closeable c) {
        try {
            if (c != null) c.close();
        } catch (Exception ignore) {            /* Do Nothing */
        }
    }

    @Override
    public long remainingCapacity() {
        return Integer.MAX_VALUE; // fake still
    }

    @Override
    public long capacity() {
        return maxFileSize; // fake still
    }

    public long usedSize() {
        return getBackFileSize();
    }

    /**
     * file size in MB
     */
    public int getBackFileSize() {
        if (writeFileIndex >= readFileIndex) {
            return (writeFileIndex - readFileIndex + 1) * PAGE_SIZE_MB;
        } else {
            return (Integer.MAX_VALUE - readFileIndex + writeFileIndex + 2) * PAGE_SIZE_MB;
        }
    }

    @Override
    public boolean isEmpty() {
        return this.readFileIndex == this.writeFileIndex && readMbb.position() == writeMbb.position();
    }

    public boolean isBacklogAvailable() {
        return backlogAvailable;
    }

    public void shutdown() {
        // stop file cleaner
        fileCleanTimer.cancel();

        if (writeMbb != null) {
            writeMbb.force();
            unMap(writeMbb);
        }
        if (readMbb != null) {
            readMbb.force();
            unMap(readMbb);
        }
        if (indexMbb != null) {
            indexMbb.force();
            unMap(indexMbb);
        }

        closeResource(readDataChannel);
        closeResource(readDataFile);
        closeResource(writeDataChannel);
        closeResource(writeDataFile);
        closeResource(indexChannel);
        closeResource(indexFile);
    }

    @Override
    public long getOverflowCount() {
        return 0;
    }

    /**
     * Periodically delete old used file which are not in current
     * read/write window.
     *
     * @author yjfei
     */
    class FileCleanTask extends TimerTask {

        public void run() {
            try {
                File[] queueFiles = queueDir.listFiles();
                List<File> toBeDeletedFiles = new ArrayList<File>();
                if (queueFiles != null && queueFiles.length > 0) {
                    for (File queueFile : queueFiles) {
                        String fileName = queueFile.getName();
                        if (fileName.endsWith(DATA_FILE_SUFFIX)) {
                            int beginIndex = fileName.lastIndexOf('-');
                            beginIndex += 1;
                            int endIndex = fileName.lastIndexOf(DATA_FILE_SUFFIX);
                            String sIndex = fileName.substring(beginIndex, endIndex);
                            int index = Integer.parseInt(sIndex);
                            if (readFileIndex <= writeFileIndex) {
                                if (index < readFileIndex || index > writeFileIndex) {
                                    toBeDeletedFiles.add(queueFile);
                                }
                            } else {
                                if (index < readFileIndex && index > writeFileIndex) {
                                    toBeDeletedFiles.add(queueFile);
                                }
                            }
                        }
                    }
                }

                for (File file : toBeDeletedFiles) {
                    file.delete();
                    logger.info("file clean task cleaned file " + file.getName());
                }

            } catch (Throwable e) {
                logger.warn("throwable from FileCleaner", e);
            }

        }
    }

}
