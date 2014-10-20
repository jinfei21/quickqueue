package com.ctrip.quickqueue.factory;

import com.ctrip.quickqueue.beans.DefaultRequest;
import com.ctrip.quickqueue.intf.IRequest;

public class RequestBuilder {

	public static <T> IRequest<T> buildRequest(){
		return new DefaultRequest<T>();
	}
}
