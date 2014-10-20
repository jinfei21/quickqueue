package com.ctrip.quickqueue.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.annotation.Command;
import com.ctrip.quickqueue.annotation.Order;
import com.ctrip.quickqueue.beans.DefaultScanner;
import com.ctrip.quickqueue.constant.QuickQueueConf;
import com.ctrip.quickqueue.factory.DefaultObjectFactory;
import com.ctrip.quickqueue.intf.IApplication;
import com.ctrip.quickqueue.intf.IExceptionFilter;
import com.ctrip.quickqueue.intf.IHandler;
import com.ctrip.quickqueue.intf.IObjectFactory;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IRequestFilter;
import com.ctrip.quickqueue.intf.IResponse;
import com.ctrip.quickqueue.intf.IResponseFilter;
import com.ctrip.quickqueue.intf.IScanner;
import com.sun.jersey.api.NotFoundException;

public abstract class AbstractApplication implements IApplication{

    private Logger LOGGER = LoggerFactory.getLogger(AbstractApplication.class);
    
    protected QuickQueueConf quickQueueConf;
    protected Set<Class<? extends IRequestFilter>> requestFilterClasses;
    protected Set<Class<? extends IResponseFilter>> responseFilterClasses;
    protected Set<Class<? extends IExceptionFilter>> exceptinFilterClasses;
    protected Set<Class<? extends IHandler>> handlerClasses;
    
    protected IObjectFactory objectFactory;
    protected IScanner scanner = new DefaultScanner();
    protected Set<Class> classes = new CopyOnWriteArraySet<Class>();;
    protected Map<Class<?>,Object> singletons  = new ConcurrentHashMap<Class<?>, Object>();
    
    
    protected List<IRequestFilter> requestFilters = new ArrayList<IRequestFilter>();
    protected List<IResponseFilter> responseFilters = new CopyOnWriteArrayList<IResponseFilter>();
    protected List<IExceptionFilter> exceptionFilters = new CopyOnWriteArrayList<IExceptionFilter>();
    protected List<IHandler> handlers = new CopyOnWriteArrayList<IHandler>();

    
    
    protected AbstractApplication(QuickQueueConf quickQueueConf){
    	this.quickQueueConf = quickQueueConf;
    	init();
    }
    
    private void init(){
    	initObjectFactory();
    	loadClasses();
    	loadRequestFilters();
    	loadResponseFilters();
    	loadExceptionFilters();
    	loadHandlers();
    }
    
    
    private void initObjectFactory(){
        if (quickQueueConf.getObjectFactory() != null && !"".equals(quickQueueConf.getObjectFactory())) {
            try {
                objectFactory = (IObjectFactory) (Class.forName(quickQueueConf.getObjectFactory())).newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Load object factory error: " + quickQueueConf.getObjectFactory(), e);
            }
        } else {
            objectFactory = new DefaultObjectFactory();
        }
    }
    
    private void loadClasses(){
    	String packagesPath = quickQueueConf.getPackages();
    	String[] packages = packagesPath.split(",");
    	
    	requestFilterClasses = scanner.scan(packages, IRequestFilter.class);
    	responseFilterClasses = scanner.scan(packages, IResponseFilter.class);
    	exceptinFilterClasses = scanner.scan(packages, IExceptionFilter.class);
    	handlerClasses = scanner.scan(packages, IHandler.class);
    	
    	classes.addAll(requestFilterClasses);
    	classes.addAll(responseFilterClasses);
    	classes.addAll(exceptinFilterClasses);
    	classes.addAll(handlerClasses);
    	
    }
    
    private void loadRequestFilters(){
    	for(Class<? extends IRequestFilter> clazz:requestFilterClasses){
    		try{
    			requestFilters.add(getSingletonInstance(clazz));
    		}catch(Exception e){
    			LOGGER.error("Can't instantiate request filter:"+clazz.getName(), e);
    			throw new RuntimeException(e);
    		}
    	}
    	Collections.sort(requestFilters, new Comparator<IRequestFilter>(){

			@Override
			public int compare(IRequestFilter filter1, IRequestFilter filter2) {
				Order orderAnn1 = filter1.getClass().getAnnotation(Order.class);
				if(orderAnn1 == null){
					return 1;
				}
				
				Order orderAnn2 = filter2.getClass().getAnnotation(Order.class);
				if(orderAnn2 == null){
					return -1;
				}
				return orderAnn2.value() - orderAnn1.value();
			}
    		
    	});
    }
    
    private void loadResponseFilters(){
    	for(Class<? extends IResponseFilter> clazz:responseFilterClasses){
    		try{
    			responseFilters.add(getSingletonInstance(clazz));
    		}catch(Exception e){
    			LOGGER.error("Can't instantiate response filter: " + clazz.getName(), e);
                throw new RuntimeException(e);
    		}
    	}
    }
    
    private void loadExceptionFilters(){
    	for(Class<? extends IExceptionFilter> clazz:exceptinFilterClasses){
    		try{
    			exceptionFilters.add(getSingletonInstance(clazz));
    		}catch(Exception e){
    			LOGGER.error("Can't instantiate exception filter:"+clazz.getName(), e);
    			throw new RuntimeException(e);
    		}
    	}
    }
    
    private void loadHandlers(){
    	for(Class<? extends IHandler> clazz:handlerClasses){
    		try{
    			handlers.add(getSingletonInstance(clazz));
    		}catch(Exception e){
    			LOGGER.error("Can't instantiate handler:"+clazz.getName(), e);
    		}
    	}
    }
    
    
    private final Object lock2 = new Object();
    
    protected <T> T getSingletonInstance(Class<T> clazz) throws Exception{
    	Object instance = singletons.get(clazz);
    	if(instance == null){
    		synchronized (lock2) {
    			instance = singletons.get(clazz);
    			if(instance == null){
    				instance = objectFactory.getInstance(clazz);
    				singletons.put(clazz, instance);
    			}
			}
    	}
    	
    	return (T) instance;
    }
    
    
    private Map<String,Class<? extends IHandler>> requestCommandMap = new ConcurrentHashMap<String,Class<? extends IHandler>>();
    private final Object lock = new Object();
    
    protected IHandler getHandler(IRequest request) throws Exception{
    	String command = request.getCommand();
    	Class<? extends IHandler> handlerClazz = requestCommandMap.get(command);
    	if(handlerClazz == null){
    		synchronized (lock) {
    			handlerClazz = requestCommandMap.get(command);
    			if(handlerClazz == null){
    				for(Class<? extends IHandler> handlerClass:handlerClasses){
    					Command commandAnnotation = handlerClass.getAnnotation(Command.class);
    					if(commandAnnotation != null && commandAnnotation.value().equals(command)){
    						handlerClazz = handlerClass;
    						requestCommandMap.put(command, handlerClazz);
    						break;
    					}
    				}
    			}
			}
    	}
    	
    	if(handlerClazz == null){
    		LOGGER.warn("Can't find the handler for command:[" + command + "]");
    		throw new NotFoundException(command);
    	}
    	
    	return getSingletonInstance(handlerClazz);
    }
    
    @Override
    public Set<Class> getClasses() {
        return classes;
    }

    @Override
    abstract public IResponse handle(IRequest request);
    
    
}
