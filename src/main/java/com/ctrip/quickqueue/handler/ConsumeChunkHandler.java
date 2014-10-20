package com.ctrip.quickqueue.handler;

import java.util.Map;

import com.ctrip.quickqueue.annotation.Command;
import com.ctrip.quickqueue.annotation.Inject;
import com.ctrip.quickqueue.annotation.Order;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.constant.Status;
import com.ctrip.quickqueue.factory.ResponseBuilder;
import com.ctrip.quickqueue.intf.IHandler;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IResponse;
import com.ctrip.quickqueue.intf.IService;


@Command(Constant.CONSUME_CHUNK)
public class ConsumeChunkHandler implements IHandler{

    @Inject(template="consume")
    private Map<String, IService> queueServiceMap;
    
	@Override
	public IResponse handle(IRequest request) {

		String queue = (String)request.getBody();
		byte[] content = queueServiceMap.get(queue).consume();
		IResponse response = ResponseBuilder.buildResponse();
		response.setResponseStatus(Status.SUCCESS);
		response.setBody(content);
		return response;
	}

}