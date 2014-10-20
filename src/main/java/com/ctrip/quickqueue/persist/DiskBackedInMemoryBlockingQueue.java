package com.ctrip.quickqueue.persist;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.intf.IPersistent;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An in memory blocking queue backed by memory mapped disk file.
 *
 * @param <T>
 * @author yjfei
 */
public class DiskBackedInMemoryBlockingQueue<T extends Serializable> implements IPersistent<T> {

    private final static Logger logger = LoggerFactory.getLogger(DiskBackedInMemoryBlockingQueue.class);

    // in memory queue
    private BlockingQueue<T> inMemoryQueue;

    // on disk persistent queue
    private IPersistent persistentQueue;
    private Object producerLock = new Object();

    private AtomicLong overflowCount = new AtomicLong(0);

    // reload the data on disk into in memeory queue
    private Reloader reloader;
    private int maxMemoryElementCount;

    /**
     * Constructor
     *
     * @param dir,                   queue directory
     * @param queueName,             the name of the queue
     * @param maxMemoryElementCount, max number of element allowed in the in memory blocking queue.
     * @param maxFileSize,           max size(in MB) of the disk file allowed
     * @throws java.io.IOException
     */
    public DiskBackedInMemoryBlockingQueue(String dir, String queueName, int maxMemoryElementCount, int maxFileSize) throws IOException {
        this.persistentQueue = new MappedFileQueue(dir, queueName, maxFileSize);
        this.maxMemoryElementCount = maxMemoryElementCount;
        this.inMemoryQueue = new ArrayBlockingQueue<T>(this.maxMemoryElementCount);
        this.reloader = new Reloader();
        this.reloader.start();
    }

    @Override
    public boolean produce(T t) {
        boolean success = this.inMemoryQueue.offer(t);
        if (!success) {
            overflowCount.incrementAndGet();
            success = persistentQueue.produce(t);
            if (success) {
                synchronized (producerLock) {
                    producerLock.notify();
                }
            }
        }
        return success;
    }

    @Override
    public T consume() {
        T t = this.inMemoryQueue.poll();
        if (t != null) return t;
        t = (T) this.persistentQueue.consume();
        return t;
    }

    @Override
    public long remainingCapacity() {
        return this.maxMemoryElementCount - inMemoryQueue.size();
    }

    @Override
    public long capacity() {
        return this.maxMemoryElementCount;
    }

    @Override
    public long usedSize() {
        return inMemoryQueue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.inMemoryQueue.isEmpty() && this.persistentQueue.isEmpty();
    }

    private void persistInMemoryElement() {
        T t = inMemoryQueue.poll();
        while (t != null) {
            this.persistentQueue.produce(t);
            t = inMemoryQueue.poll();
        }
    }

    @Override
    public void shutdown() {
        // stop reloader
        reloader.setRunning(false);
        while (!reloader.isStopped()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                // do nothing
            }
        }

        this.persistInMemoryElement();

        this.persistentQueue.shutdown();

    }

    @Override
    public long getOverflowCount() {
        return overflowCount.get();
    }

    @Override
    public int getBackFileSize() {
        return this.persistentQueue.getBackFileSize();
    }

    class Reloader extends Thread {
        private volatile boolean running = true;
        private volatile boolean stopped = false;

        public void run() {
            T t = null;
            while (running) {
                try {
                    if (t == null) {
                        t = (T) persistentQueue.consume();
                    }
                    if (t != null) {// got element in persistent queue
                        boolean success = inMemoryQueue.offer(t);
                        if (success) { // consumed
                            t = null;
                        } else { // in memory queue is full
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                // ignore
                            }
                        }
                    } else { // nothing to consume in persistent queue
                        try {
                            synchronized (producerLock) {
                                producerLock.wait(2000);
                            }
                        } catch (InterruptedException e) {
                            // ignore
                        }
                    }
                } catch (Throwable e) {
                    logger.warn("throwable from reloader", e);
                }

            }
            if (t != null) { // return to the persistent queue
                persistentQueue.produce(t);
            }
            stopped = true;
            logger.info("Reloader exit the doStart loop");
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public boolean isStopped() {
            return this.stopped;
        }

    }

}
