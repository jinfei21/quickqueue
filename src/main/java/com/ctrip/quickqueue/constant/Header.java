package com.ctrip.quickqueue.constant;

public enum Header {
	APPID("appID"),
	IP("IP"),
	HOST("host"),
	COMPRESS("compress"),
	SERIALIZE("serialize"),
	COMMAND("command"),
	ROUTE("route");
	
	private String code;
	
	Header(String code){
		this.code = code;
	}
	
	public String getCode(){
		return this.code;
	}
	
	public void setCode(String code){
		this.code = code;
	}

}
