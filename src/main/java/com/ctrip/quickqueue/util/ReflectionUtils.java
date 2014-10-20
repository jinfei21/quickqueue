package com.ctrip.quickqueue.util;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReflectionUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectionUtils.class);
	private static final Class<?>[] EMPTY_ARRAY = new Class[]{};
 	private static final Map<Class<?>,Constructor<?>> CONSTRUCTOR_CACHE = new ConcurrentHashMap<Class<?>,Constructor<?>>();
	
	
	
	public static <T> T newInstatnce(Class<T> theClass){
		T result;
		try{
			Constructor<T> method = (Constructor<T>) CONSTRUCTOR_CACHE.get(theClass);
			if(method == null){
				method = theClass.getDeclaredConstructor(EMPTY_ARRAY);
				method.setAccessible(true);
				CONSTRUCTOR_CACHE.put(theClass, method);
			}
			result = method.newInstance();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		return result;
	}
	
	public static <T> T newInstance(String theClass){
		T result;
		try{
			Class clazz = Class.forName(theClass);
			Constructor<T> method = clazz.getConstructor(EMPTY_ARRAY);
			method.setAccessible(true);
			result = (T)method.newInstance();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
		
		return result;
	}
}
