package com.ctrip.quickqueue;

public class receive {

	public static void main(String args[]){
		QueueAgent agent=QueueAgent.getAgent("http://127.0.0.1:63200/chunks/");
		
		while(true){
		QueueMessage  msg = agent.get("hbase");
		
		System.out.println(new String(msg.getBody()));
		}
	}
}
