package com.ctrip.quickqueue.intf;

public interface IExceptionFilter <T extends Throwable> {
    public void filter(T exception,IRequest request,IResponse response);
}
