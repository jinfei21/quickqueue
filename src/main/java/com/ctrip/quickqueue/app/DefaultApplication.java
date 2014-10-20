package com.ctrip.quickqueue.app;


import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.constant.QuickQueueConf;
import com.ctrip.quickqueue.constant.Status;
import com.ctrip.quickqueue.factory.ResponseBuilder;
import com.ctrip.quickqueue.intf.IExceptionFilter;
import com.ctrip.quickqueue.intf.IHandler;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IRequestFilter;
import com.ctrip.quickqueue.intf.IResponse;
import com.ctrip.quickqueue.intf.IResponseFilter;

public class DefaultApplication extends AbstractApplication{
	
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public DefaultApplication(QuickQueueConf quickQueueConf){
    	super(quickQueueConf);
    }

	@Override
	public IResponse handle(IRequest request) {
		// TODO Auto-generated method stub
		return _handle(request);
	}
	
	private IResponse _handle(IRequest request){
		
		IResponse response = null;
		
		Date begin = new Date();
		String appID = null;
		String agentIP = null;
		boolean isSkip = false;
		
		try{
			
			executeRequestFilters(request);
			
			IHandler handler = getHandler(request);
			
			response = handler.handle(request);
			
			executeResponseFiltes(response);
			
		}catch(Throwable e){
			response = executeExceptionFilter(e,request,response);
			
		}
		
		return response;
	}
	
	private IResponse executeExceptionFilter(Throwable e,IRequest request,IResponse response){
		for(IExceptionFilter exceptionFilter:exceptionFilters){
			exceptionFilter.filter(e, request, response);
		}
		
		IResponse res = ResponseBuilder.buildResponse();
		
		res.setResponseStatus(Status.FAILURE);
		res.setThrowable(e);
		
		return res;
	}
	
	private void executeResponseFiltes(IResponse response){
		for(IResponseFilter responseFilter:responseFilters){
			responseFilter.filter(response);
		}
	}
	
	private void executeRequestFilters(IRequest request){
		for(IRequestFilter requestFilter:requestFilters){
			requestFilter.filter(request);
		}
	}
	
	
	private <T> boolean checkIsNull(T... ts){
		for(T t:ts){
			if(t == null){
				return true;
			}
		}
		return false;
	}

}
