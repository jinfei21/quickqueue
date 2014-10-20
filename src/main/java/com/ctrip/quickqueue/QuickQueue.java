package com.ctrip.quickqueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.app.DefaultApplication;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.constant.QuickQueueConf;
import com.ctrip.quickqueue.intf.IApplication;
import com.ctrip.quickqueue.rest.server.QuickQueueServer;
import com.ctrip.quickqueue.util.Configuration;
import com.ctrip.quickqueue.util.DeamonWatcher;

/*
 * @author:yjfei
 * 
 */

public class QuickQueue {
	
	public  static Logger logger = LoggerFactory.getLogger(QuickQueue.class);
	
	public static void main(String args[]){
		
		try{
			initConfig();
			IApplication application = buildApplication();
			startRestQuickQueue(application);
		}catch(Throwable e){
			System.out.print("start quickqueue error!");
			e.printStackTrace();
			DeamonWatcher.exit(-1);
		}
		
	}
	
	private static void startRestQuickQueue(IApplication application){
		int port = Configuration.getInt(Constant.QUICKQUEUE_REST_PORT);
		String resources = Configuration.get(Constant.QUICKQUEUE_REST_RESOURCE);
		QuickQueueServer restServer = new QuickQueueServer(resources, port, application);
		restServer.setServerName("QuickQueue");
		restServer.start();
	}
	
	private static IApplication buildApplication(){
		QuickQueueConf quickQueueConf = new QuickQueueConf(Configuration.get(Constant.QUICKQUEUE_SCAN_PACKAGE),Configuration.get(Constant.QUICKQUEUE_OBJECT_FACTORY));
		
		return new DefaultApplication(quickQueueConf);
		
	}
	
	private static void initConfig(){
		
		Configuration.addResource(Constant.QUEUE_CONFIG);
	}

}
