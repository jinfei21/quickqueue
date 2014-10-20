package com.ctrip.quickqueue.constant;

public enum Status {
	SUCCESS(1),
	FAILURE(2);
	
	private int status;
	
	Status(int status) {
		this.status = status;
	}
	
	public int getStatus(){
		return status;
	}
}
