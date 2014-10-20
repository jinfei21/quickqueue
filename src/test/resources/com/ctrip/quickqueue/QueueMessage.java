package com.ctrip.quickqueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.ctrip.quickqueue.util.ByteUtils;
import com.ctrip.quickqueue.util.HeaderUtils;

public class QueueMessage {
	
	private Header header;
	
	private byte[] body;
	
	public QueueMessage(String route,byte[] body){
		this.body = body;
		this.header = new Header("123", route);
	}
	
	public QueueMessage(String route,String body){
		this.body = body.getBytes();
		this.header = new Header("123", route);
	}
	
	public QueueMessage(byte content[]){
		this.header = new Header(content);
		this.body = new byte[content.length -100];
		System.arraycopy(content, 100, body, 0, body.length);
	}
	
	public class Header {
		private String APPID;
		private String Host;
		private String IP;
		private String route;
		private byte version;
		private byte serialize;
		private long PID;
		
		public Header(String APPID,String route){
			this.APPID = APPID;
			this.route = route;
		}
		
		public Header(byte header[]){
			if(header.length < 100) return;
			this.version = ByteUtils.toByte(header, HeaderUtils.OFFSET_OF_VERSION);
			this.APPID = ByteUtils.toString(header, HeaderUtils.OFFSET_OF_APPID, HeaderUtils.SIZE_OF_APPID);
			this.PID = ByteUtils.toLong(header, HeaderUtils.OFFSET_OF_PID);
			this.IP =  ByteUtils.toString(header, HeaderUtils.OFFSET_OF_IP, HeaderUtils.SIZE_OF_IP);
			this.Host = ByteUtils.toString(header, HeaderUtils.OFFSET_OF_HOST, HeaderUtils.SIZE_OF_HOST);
			this.route = ByteUtils.toString(header, HeaderUtils.OFFSET_OF_ROUTE, HeaderUtils.SIZE_OF_ROUTE);
			this.serialize = ByteUtils.toByte(header, HeaderUtils.OFFSET_OF_SERIALIZE);
		}
		
		public byte[] toBytes(){
			byte[] headerBytes=new byte[100];
			 
	        ByteUtils.setByte(headerBytes, HeaderUtils.OFFSET_OF_VERSION, (byte) 1);
	        ByteUtils.setString(headerBytes, HeaderUtils.OFFSET_OF_APPID, HeaderUtils.SIZE_OF_APPID, APPID);
	        ByteUtils.setLong(headerBytes, HeaderUtils.OFFSET_OF_PID, 122L);
	        ByteUtils.setString(headerBytes, HeaderUtils.OFFSET_OF_IP, HeaderUtils.SIZE_OF_IP, "127.0.0.1");
	        ByteUtils.setString(headerBytes, HeaderUtils.OFFSET_OF_HOST, HeaderUtils.SIZE_OF_HOST, "localhost");
	        ByteUtils.setString(headerBytes, HeaderUtils.OFFSET_OF_ROUTE, HeaderUtils.SIZE_OF_ROUTE, route);
	        ByteUtils.setByte(headerBytes, HeaderUtils.OFFSET_OF_SERIALIZE,(byte) 1);
	        return headerBytes;
		}
		
	}
	
	public byte[] toBytes() throws IOException{
		ByteArrayOutputStream buf =  new ByteArrayOutputStream();
		buf.write(header.toBytes());
		buf.write(body);
		return buf.toByteArray();
	}

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

}
