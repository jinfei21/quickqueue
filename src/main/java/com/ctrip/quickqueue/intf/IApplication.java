package com.ctrip.quickqueue.intf;

import java.util.Set;

public interface IApplication {
	
    Set<Class> getClasses();

    IResponse handle(IRequest request);
}
