package com.ctrip.quickqueue.filter;

import com.ctrip.quickqueue.annotation.Order;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.constant.Header;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IRequestFilter;
import com.ctrip.quickqueue.util.HeaderUtils;

@Order(99)
public class HeaderFilter implements IRequestFilter{

	@Override
	public void filter(IRequest request) {
		
		if(Constant.PRODUCE_CHUNK.equals(request.getCommand())){
			
			byte[] data = (byte[]) request.getBody();
            byte[] headerBytes = HeaderUtils.getHeader(data);
            request.addHeader(Header.APPID.getCode(), HeaderUtils.getAppID(headerBytes));
            request.addHeader(Header.IP.getCode(), HeaderUtils.getIP(headerBytes));
            request.addHeader(Header.HOST.getCode(), HeaderUtils.getHost(headerBytes));
            request.addHeader(Header.ROUTE.getCode(), HeaderUtils.getRoute(headerBytes));
		}
			
	}


}
