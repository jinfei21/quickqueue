package com.ctrip.quickqueue.filter;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ctrip.quickqueue.constant.Status;
import com.ctrip.quickqueue.intf.IExceptionFilter;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IResponse;

public class ExceptionFilter implements IExceptionFilter<Throwable>{

	@Override
	public void filter(Throwable exception, IRequest request, IResponse response) {
		
		response.setErrorMsg(getStackTrace(exception));
		response.setResponseStatus(Status.FAILURE);
	}
	
	private String getStackTrace(Throwable t){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return sw.toString();
	}

}
