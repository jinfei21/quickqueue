package com.ctrip.quickqueue.intf;

import java.util.Set;

public interface IScanner {
	<T> Set<Class<? extends T>> scan(String[] packages,Class<T> clazz);
}
