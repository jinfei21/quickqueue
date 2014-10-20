package com.ctrip.quickqueue.beans;

import java.util.HashMap;
import java.util.Map;

import com.ctrip.quickqueue.constant.Header;
import com.ctrip.quickqueue.intf.IPacket;

public class DefaultPacket<T> implements IPacket<T> {

	private Map<String,String> headers = new HashMap<String,String>();
	private T body;
	
	protected DefaultPacket(){
		
	}
	
	@Override
	public Map<String, String> getHeaders() {
		
		return headers;
	}

	@Override
	public void setHeaders(Map<String, String> headers) {
		
		headers.clear();
		headers.putAll(headers);
	}

	@Override
	public T getBody() {
		
		return body;
	}

	@Override
	public void setBody(T body) {
		this.body = body;
		
	}

	@Override
	public String getHeader(String headerName) {
		
		return headers.get(headerName);
	}

	@Override
	public void addHeader(String headerName, String headerValue) {
		headers.put(headerName, headerValue);
		
	}

	@Override
	public String getHost() {
		
		return getHeader(Header.HOST.getCode());
	}

	@Override
	public String getIP() {
		return getHeader(Header.IP.getCode());
	}

	@Override
	public String getCompress() {
		return getHeader(Header.COMPRESS.getCode());
	}

	@Override
	public String getSerialize() {
		return getHeader(Header.SERIALIZE.getCode());
	}

	@Override
	public String getCommand() {
		return getHeader(Header.COMMAND.getCode());
	}

	@Override
	public String getRoute() {
		return getHeader(Header.ROUTE.getCode());
	}

	@Override
	public String getAppId() {
		return getHeader(Header.APPID.getCode());
	}

}
