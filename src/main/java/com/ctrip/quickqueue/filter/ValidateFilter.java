package com.ctrip.quickqueue.filter;

import com.ctrip.quickqueue.annotation.Order;
import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IRequestFilter;
import com.ctrip.quickqueue.util.IPUtils;

@Order(98)
public class ValidateFilter implements IRequestFilter{

	@Override
	public void filter(IRequest request) {
		
		if(Constant.PRODUCE_CHUNK.equals(request.getCommand())){
			if(!IPUtils.isValidIP(request.getIP())){
				throw new RuntimeException("invalid IP!");
			}
		}
		
	}

}
