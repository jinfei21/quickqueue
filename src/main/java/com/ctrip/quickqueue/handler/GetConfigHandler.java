package com.ctrip.quickqueue.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import com.ctrip.quickqueue.annotation.Command;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.factory.ResponseBuilder;
import com.ctrip.quickqueue.intf.IHandler;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IResponse;
import com.ctrip.quickqueue.util.Configuration;



@Command(Constant.GET_CONFIG)
public class GetConfigHandler implements IHandler{

	@Override
	public IResponse handle(IRequest request) {
		// TODO Auto-generated method stub
		Properties props = Configuration.getProperties(Constant.QUEUE_CONFIG);
		Map<String,String> map = new HashMap<String,String>();
		for(Entry<Object,Object> entry:props.entrySet()){
			map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		
		IResponse response = ResponseBuilder.buildResponse();
		response.setBody(map);
		return response;
		
	}

}
