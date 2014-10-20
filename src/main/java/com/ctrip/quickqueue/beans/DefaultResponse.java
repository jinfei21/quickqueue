package com.ctrip.quickqueue.beans;

import com.ctrip.quickqueue.constant.Status;
import com.ctrip.quickqueue.intf.IResponse;

public class DefaultResponse<T>  extends DefaultPacket<T>  implements IResponse<T>{
	
    private Status status;
    private String errorMsg;
    
    public DefaultResponse() {
    }

    protected DefaultResponse(Status status) {
        this.status = status;
    }

    @Override
    public Status getResponseStatus() {
        return this.status;
    }

    @Override
    public void setResponseStatus(Status status) {
        this.status = status;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @Override
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.errorMsg = throwable.getMessage();
    }

}
