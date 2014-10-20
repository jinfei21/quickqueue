package com.ctrip.quickqueue.intf;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.ctrip.quickqueue.util.DeamonWatcher;

public abstract class AbstractServer {

	private String name;
	private static ExecutorService executor = Executors.newFixedThreadPool(2); 
	
	public void setServerName(String name){
		this.name = name;
	}
	
	public String getServerName(){
		return this.name;
	}
	
	public void start(){
		try{
			
			executor.submit(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						doStartup();
					} catch (Exception e) {
					}
				}
			});
			
			
			DeamonWatcher watcher = DeamonWatcher.getInstance(name);
			watcher.init();
			
		}catch(Throwable e){
			System.out.print("Throw error during start <" + name + "> server, server will shutdown!");
			e.printStackTrace();
			DeamonWatcher.exit(-1);
		}
	}
	
	public void shutdown(){
		executor.shutdown();
		try {
			doShutdown();
		} catch (Exception e) {
		}
	}
	public abstract void doShutdown() throws Exception;
	
	public abstract void doStartup() throws Exception;
}
