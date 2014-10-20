package com.ctrip.quickqueue.factory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.annotation.Inject;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.intf.IObjectFactory;
import com.ctrip.quickqueue.intf.IService;
import com.ctrip.quickqueue.service.QuickQueueService;
import com.ctrip.quickqueue.util.Configuration;

public class DefaultObjectFactory implements IObjectFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultObjectFactory.class);
	private static Map<String,Object> map = null;
	
	public DefaultObjectFactory(){
		//初始化模版
		this.map = new HashMap<String,Object>();
		Map<String, IService> queueProduce = new ConcurrentHashMap<String, IService>();
		map.put("produce", queueProduce);
		
		Map<String, IService> queueConsume = new ConcurrentHashMap<String, IService>();
		map.put("consume", queueConsume);
		
		Map<String, Pattern> queueRouteMap = new ConcurrentHashMap<String, Pattern>();
		map.put("route", queueRouteMap);
		
		String[] queues = Configuration.get(Constant.QUICKQUEUE_NAMES).trim().split("\\s*,\\s*");
		String queueDir = Configuration.get(Constant.QUICKQUEUE_DISKQUEUE_DIR);
		for(String queue:queues){
			try{
				queueProduce.put(queue, new QuickQueueService(queue, queueDir));
				queueConsume.put(queue, new QuickQueueService(queue, queueDir));
				String route = Configuration.get(Constant.QUICKQUEUE_ROUTE_PREFIX+queue);
				queueRouteMap.put(queue, Pattern.compile(route));
			}catch(Throwable e){
				LOGGER.error("create template fail:"+queue, e);
			}
		}
	}
	
	@Override
	public <T> T getInstance(Class<T> clazz) throws Exception {
		T instance = clazz.newInstance();
		for(Field f:clazz.getDeclaredFields()){
			for(Annotation a:f.getAnnotations()){
				if(a.annotationType().isAssignableFrom(Inject.class)){
				    f.setAccessible(true);
				    f.set(instance, map.get(((Inject)a).template()));
				}
			}
		}
		
		return instance;
	}

}
