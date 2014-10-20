package com.ctrip.quickqueue.factory;

import com.ctrip.quickqueue.beans.DefaultResponse;
import com.ctrip.quickqueue.intf.IResponse;

public class ResponseBuilder {

    public static <T> IResponse<T> buildResponse() {
        return new DefaultResponse<T>();
    }
}
