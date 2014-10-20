package com.ctrip.quickqueue.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeamonWatcher {

	private static DeamonWatcher instance = null;
	private static String name;
	private boolean needInit = false;


	public synchronized static DeamonWatcher getInstance(String name){
		if(instance == null){
			instance = new DeamonWatcher(name);
		}
		return instance;
	}
	
	public synchronized void init() throws IOException{
		if(!needInit){
			PIDFile pFile = new PIDFile();
			ShutdownHookManager.get().addShutdownHook(pFile, 8);
			needInit = true;
		}
	}

	private DeamonWatcher(String name){
		this.name = name;
	}
	
	public static void exit(int status){
		System.exit(status);
	}

	private static class PIDFile implements Runnable{
		private final static Logger LOGGER = LoggerFactory.getLogger(PIDFile.class);

		private static FileLock lock = null;
		private static FileOutputStream pFileStream = null;
		private static String pFileName;
		
		
		public PIDFile() throws IOException{
			try{
				init();
			}catch(IOException e){
				clean();
				throw e;
			}
		}
		
		public void init() throws IOException{
			String pidStr = ManagementFactory.getRuntimeMXBean().getName();
			
			String[] items = pidStr.split("@");
			String pid = items[0];
			String home = System.getenv("user.dir");
			if(home == null){
				home = ".";
			}
			if(!home.endsWith("/")){
				home = home + File.separator;
			}
			
			StringBuilder pFilePath = new StringBuilder();
			pFilePath.append(home).append(name).append(".pid");
			pFileName = pFilePath.toString();
			
			try{
				File pFile = new File(pFileName);
				
				pFileStream = new FileOutputStream(pFile);
				
				pFileStream.write(pid.getBytes());
				
				pFileStream.flush();
				
				FileChannel channel = pFileStream.getChannel();
				
				PIDFile.lock = channel.tryLock();
				
				if(PIDFile.lock != null){
					LOGGER.debug("initial pid file succeeded..");
				}else{
					throw new IOException();
				}
			}catch(IOException e){
				LOGGER.error("initial pid file failed..", e);
				throw e;
			}
			
			
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			LOGGER.info("clean pid starting ...");
			clean();
		}
		
		private void clean(){
			File pFile = new File(pFileName);
				
			if(!pFile.exists()){
				LOGGER.warn("delete pid file,no such file:"+pFileName);
			}else{
				try{
					lock.release();
					pFileStream.close();
				}catch(IOException e){
					LOGGER.error("unable to release file lock:"+pFileName);
				}
				if(!pFile.delete()){
					LOGGER.warn("delete pid file fail, "+pFileName);
				}
			}
		}
		
		
	}
}

