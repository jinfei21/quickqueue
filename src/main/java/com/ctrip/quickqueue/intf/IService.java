package com.ctrip.quickqueue.intf;

public interface IService {
    /**
     * Produce data, then insert into memory queue, if memory queue is overflow, then map data to disk file.
     *
     * @param data byte array data
     */
    public void produce(byte[] data);

    /**
     * Consume byte array data from queue
     *
     * @return byte array data
     */
    public byte[] consume();

    /**
     * Get memory queue used size
     *
     * @return used size
     */
    public long getMemUsedSize();

    /**
     * Get memory queue used size
     *
     * @return overflow count
     */
    public long getMemQueueOverflow();

    /**
     * Get memory queue remaining capacity
     *
     * @return remaining capacity
     */
    public long getMemQueueRemainingCapacity();

    /**
     * Get memory queue remaining capacity
     *
     * @return back file size
     */
    public int getBackFileSize();

    /**
     * Get overflow for queue
     *
     * @return overflow count map
     */
    public Long getMemFailCountMap();
}
