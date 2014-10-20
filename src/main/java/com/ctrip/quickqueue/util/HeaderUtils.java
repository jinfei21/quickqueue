package com.ctrip.quickqueue.util;

public class HeaderUtils {

	public final static int SIZE_OF_VERSION = 1;
	public final static int SIZE_OF_APPID=8;
	public final static int SIZE_OF_PID=Long.SIZE/Byte.SIZE;
	public final static int SIZE_OF_IP=16;
	public final static int SIZE_OF_HOST=32;
	public final static int SIZE_OF_ROUTE=16;
	
	public final static int OFFSET_OF_VERSION = 0;
	public final static int OFFSET_OF_APPID = OFFSET_OF_VERSION;
	public final static int OFFSET_OF_PID=OFFSET_OF_APPID+SIZE_OF_APPID;
	public final static int OFFSET_OF_IP=OFFSET_OF_PID+SIZE_OF_PID;
	public final static int OFFSET_OF_HOST=OFFSET_OF_IP+SIZE_OF_IP;
	public final static int OFFSET_OF_ROUTE=OFFSET_OF_HOST+SIZE_OF_HOST;
	public final static int OFFSET_OF_SERIALIZE=OFFSET_OF_ROUTE+SIZE_OF_ROUTE;
	
	public static byte getVersion(byte[] header){
		return ByteUtils.toByte(header, OFFSET_OF_VERSION);
	}
	
    public static String getAppID(byte[] header) {
        return ByteUtils.toString(header, OFFSET_OF_APPID, SIZE_OF_APPID);
    }

    public static String getIP(byte[] header) {
        return ByteUtils.toString(header, OFFSET_OF_IP, SIZE_OF_IP);
    }

    public static String getHost(byte[] header) {
        return ByteUtils.toString(header, OFFSET_OF_HOST, SIZE_OF_HOST);
    }

    public static String getRoute(byte[] header) {
        return ByteUtils.toString(header, OFFSET_OF_ROUTE, SIZE_OF_ROUTE);
    }

    public static byte getSerialize(byte[] header) {
        return ByteUtils.toByte(header, OFFSET_OF_SERIALIZE);
    }
    
    public static byte[] getHeader(byte[] data) {
        byte[] header = new byte[100];
        System.arraycopy(data, 0, header, 0, header.length);
        return header;
    }

    public static byte[] getBody(byte[] data) {
        byte[] body = new byte[data.length - 100];
        System.arraycopy(data, 100, body, 0, body.length);
        return body;
    }
	
	
}
