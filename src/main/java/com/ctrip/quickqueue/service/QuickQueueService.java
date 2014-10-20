package com.ctrip.quickqueue.service;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.intf.IPersistent;
import com.ctrip.quickqueue.intf.IService;
import com.ctrip.quickqueue.persist.DiskBackedInMemoryBlockingQueue;
import com.ctrip.quickqueue.persist.MappedFileQueue;
import com.ctrip.quickqueue.util.Configuration;
import com.ctrip.quickqueue.util.ShutdownHookManager;

public class QuickQueueService implements IService {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuickQueueService.class);
	
	
	private IPersistent<byte[]> queue;
	private final Object lock = new Object();
	private volatile boolean running = false;
	private final Lock writerLock = new ReentrantLock();
	private final Lock readLock = new ReentrantLock();
	private AtomicLong memFailCount;
    private String queueName;
    private String queueDir;
    
    
    public QuickQueueService(String queueName,String queueDir)throws Exception{
    	this.queueName = queueName;
    	this.queueDir = queueDir;
    	init();
    }
    
    private void init() throws Exception{
    	if(running){
    		throw new Exception("Disk queue is running,cannot be initialized!");
    	}
    	synchronized (lock) {
			try{
				initQueue();
				ShutdownHookManager.get().addShutdownHook(new QueueFinalizer(), 9);
			}catch(IOException e){
				throw new IOException("initial disk queue error.",e);
			}
		}
    	running = true;
    }
    
	private void initQueue() throws IOException{
		int maxOnDiskFileSizeGB = Configuration.getInt(Constant.QUICKQUEUE_DISKQUEUE_MAXSIZE);
		int maxOnDiskFileSizeMB = 1024*maxOnDiskFileSizeGB;
		
		int maxInMemoryChunkCount = Configuration.getInt(Constant.QUICKQUEUE_MEMQUEUE_MAXCHUNKCOUNT);
		
		if(maxInMemoryChunkCount <= 0){
			queue = new MappedFileQueue<byte[]>(queueDir, queueName, maxOnDiskFileSizeMB);
		}else{
			queue = new DiskBackedInMemoryBlockingQueue<>(queueDir, queueName, maxInMemoryChunkCount, maxOnDiskFileSizeMB);
		}
		memFailCount = new AtomicLong(0);
	}
	
	private final class QueueFinalizer implements Runnable{
		private final Logger LOGGER = LoggerFactory.getLogger(QueueFinalizer.class);

		@Override
		public void run() {
			LOGGER.info("Finalizer persistent queue when system shutdown");
			try{
				LOGGER.info("Finalizer persistent queue,clean queue<"+queueName+">");
				queue.shutdown();
			}catch(Exception e){
				LOGGER.error("Catch exceptio during shutdown!", e);
			}
		}
		
	}
	

	@Override
	public void produce(byte[] data) {
		if(data != null){
			push(data);
		}else{
			throw new NullPointerException();
		}
		
	}

	@Override
	public byte[] consume() {
		
		return pull();
	}
	
    private void checkStatus() {
        if (!running) {
            throw new RuntimeException("Disk queue not initial!");
        }
    }
	
	private void push(byte[] data){
		checkStatus();
		boolean success = false;
		writerLock.lock();
		try{
			success = queue.produce(data);
			if(!success){
                LOGGER.warn("Insert chunk into the " + queueName + " queue fail!");
				memFailCount.incrementAndGet();
			}
		}catch(Throwable e){
			LOGGER.warn("Throw an exception when push a chunk into queue:",e);
		}finally{
			writerLock.unlock();
		}
		
		if(!success){
			throw new RuntimeException("push a chunk into a disk queue fail!");
		}
	}
	
	private byte[] pull(){
		byte[] data = null;
		readLock.lock();
		try{
			data = queue.consume();
		}catch(Throwable e){
			LOGGER.warn("Throw an exception when push a chunk into queue: ", e);
		}finally{
			readLock.unlock();
		}
		
		return data;
	}
	

	@Override
	public long getMemUsedSize() {

		return queue.usedSize();
	}

	@Override
	public long getMemQueueOverflow() {
		
		return queue.getOverflowCount();
	}

	@Override
	public long getMemQueueRemainingCapacity() {
		
		return queue.remainingCapacity();
	}

	@Override
	public int getBackFileSize() {
		
		return queue.getBackFileSize();
	}

	@Override
	public Long getMemFailCountMap() {
		
		return memFailCount.get();
	}

}
