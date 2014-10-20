package com.ctrip.quickqueue.util;

import java.lang.management.ManagementFactory;

import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MBeanUtils {
	
	private static Logger LOGGER = LoggerFactory.getLogger(MBeanUtils.class);
	
	
	public static ObjectName registerMBean(String name,Object bean){
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName objectName = getMBeanName(name);
		try{
			mbs.registerMBean(bean, objectName);
			return objectName;
		}catch(InstanceAlreadyExistsException e){
			
		}catch(Exception e){
			LOGGER.error("register MBean meet error!", e);
		}
		return null;
	}
	
	
	public static void unregisterMBean(ObjectName objectName){
		final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		if(objectName == null){
			return;
		}
		
		try{
			mbs.unregisterMBean(objectName);
		}catch(InstanceNotFoundException e){
			
		}catch(Exception e){
			LOGGER.error("unregister MBean meet error!",e);
		}
	}
	
	private static ObjectName getMBeanName(String name){
		ObjectName objectName = null;
		try{
			objectName = new ObjectName(name);
		}catch(MalformedObjectNameException e){
			LOGGER.error("get objectname meet error!", e);
		}
		return objectName;
	}

}
