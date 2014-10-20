package com.ctrip.quickqueue.intf;

import java.io.Serializable;

public interface IPersistent<T extends Serializable> {
	
	boolean produce(T t);
	
	T consume();
	
	long remainingCapacity();
	
	long capacity();

    long usedSize();
	
	boolean isEmpty();
	
	void shutdown();
	
	/**
	 * Memory overflow count
	 * 
	 * @return overflow count
	 */
	long getOverflowCount();
	
	/**
	 * back file size in MB
	 * 
	 * @return back file size
	 */
	int getBackFileSize();
	
}
