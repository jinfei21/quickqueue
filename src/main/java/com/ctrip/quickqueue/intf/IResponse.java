package com.ctrip.quickqueue.intf;

import com.ctrip.quickqueue.constant.Status;

public interface IResponse<T> extends IPacket<T> {
    public Status getResponseStatus();

    public void setResponseStatus(Status status);

    public String getErrorMsg();

    public void setErrorMsg(String errorMsg);

    public void setThrowable(Throwable throwable);
}
