package com.ctrip.quickqueue.intf;

public interface IHandler <RQ, RS> {
    IResponse<RS> handle(IRequest<RQ> request);
}
