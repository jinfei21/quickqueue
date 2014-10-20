package com.ctrip.quickqueue.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuickQueueConf {

	private static final Logger LOGGER = LoggerFactory.getLogger(QuickQueueConf.class);
    
    private  String packages;
    private  String objectFactory;
    
    public QuickQueueConf(){
    	
    }
	
    public QuickQueueConf(String packages,String objectFactory){
    	this.objectFactory = objectFactory;
    	this.packages = packages;
    }
	
	public  String getPackages(){
		return packages;
	}

	public  String getObjectFactory() {
		return objectFactory;
	}
	
	
}
