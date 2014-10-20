package com.ctrip.quickqueue.handler;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ctrip.quickqueue.annotation.Command;
import com.ctrip.quickqueue.annotation.Inject;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.constant.Header;
import com.ctrip.quickqueue.constant.Status;
import com.ctrip.quickqueue.factory.ResponseBuilder;
import com.ctrip.quickqueue.intf.IHandler;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IResponse;
import com.ctrip.quickqueue.intf.IService;


@Command(Constant.PRODUCE_CHUNK)
public class ProduceChunkHandler implements IHandler{

    @Inject(template="produce")
    private Map<String, IService> queueServiceMap;
    
    @Inject(template="route")
    private Map<String, Pattern> queueRouteMap;
    
	@Override
	public IResponse handle(IRequest request) {

		byte[] content = (byte[]) request.getBody();
		String route = request.getHeader(Header.ROUTE.getCode());
		for(String queue:queueServiceMap.keySet()){
			Pattern pattern = queueRouteMap.get(queue);
			Matcher matcher = pattern.matcher(route);
			if(matcher.matches()){
				queueServiceMap.get(queue).produce(content);
				
			}
		}
		IResponse response = ResponseBuilder.buildResponse();
		response.setResponseStatus(Status.SUCCESS);
		return response;
	}

}
