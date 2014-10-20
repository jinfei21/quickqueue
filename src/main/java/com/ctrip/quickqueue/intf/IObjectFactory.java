package com.ctrip.quickqueue.intf;

public interface IObjectFactory {

	<T> T getInstance(Class<T> clazz) throws Exception;
}
