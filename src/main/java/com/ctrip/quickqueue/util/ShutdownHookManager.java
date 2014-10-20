package com.ctrip.quickqueue.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownHookManager {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ShutdownHookManager.class);
	
	private static final ShutdownHookManager manager = new ShutdownHookManager();
	private static final Set<HookEntry> hooks = Collections.synchronizedSet(new HashSet<HookEntry>());
	private AtomicBoolean shutdownInProgress = new AtomicBoolean(false);
	
	private ShutdownHookManager(){
		
	}
	
	public static class Priority{
		public static Priority HIGH = new Priority(9);
		public static Priority LOW = new Priority(1);
		
		private int priority;
		
		Priority(int priority){
			this.priority = priority;
		}
		
		public int getPriority(){
			return this.priority;
		}
	}
	
	
	
	private static class HookEntry{
        Runnable hook;
        Priority priority;

        public HookEntry(Runnable hook, Priority priority) {
            this.hook = hook;
            this.priority = priority;
        }

        @Override
        public int hashCode() {
            return hook.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            boolean eq = false;
            if (obj != null) {
                if (obj instanceof HookEntry) {
                    eq = (hook == ((HookEntry) obj).hook);
                }
            }
            return eq;
        }
	}
	
	static {
		Runtime.getRuntime().addShutdownHook(
				new Thread(){
					@Override
					public void run(){
						manager.shutdownInProgress.set(true);
						List<Runnable> shutdownHooks = manager.getShutdownHooksInOrder();
                        for (Runnable hook : shutdownHooks) {
                            try {
                                executeShutdownHook(hook);
                            } catch (Throwable ex) {
                                LOGGER.warn("ShutdownHook '" + hook.getClass().getSimpleName() +
                                        "' failed, " + ex.toString(), ex);
                            }
                        }
					}
				}
				
		);
		
	}
	
	private static void executeShutdownHook(Runnable runnable){
		runnable.run();
	}
	
    private static void beforeExecuteShutdownHooks(List<Runnable> shutdownHooks) {
        System.out.println("Total hooks = " + shutdownHooks.size());
        for (Runnable hook : shutdownHooks) {
            System.out.println(hook);
        }
        System.out.println("Executing shutdown hooks...");
    }
    
    public static ShutdownHookManager get(){
    	return manager;
    }
	
	public List<Runnable> getShutdownHooksInOrder(){
		List<HookEntry> list;
		
		synchronized (manager.hooks) {
			list = new ArrayList<HookEntry>(manager.hooks);
		}
		
		Collections.sort(list, new Comparator<HookEntry>() {

			@Override
			public int compare(HookEntry o1, HookEntry o2) {
				
				return o2.priority.getPriority() - o1.priority.getPriority();
			}
			
		});
		
		List<Runnable> orders = new ArrayList<Runnable>();
		for(HookEntry entry:list){
			orders.add(entry.hook);
		}
		
		return orders;
	}
	
    public void addShutdownHook(Runnable shutdownHook, int priority) {
        addShutdownHook(shutdownHook, new Priority(priority));
    }

    private void addShutdownHook(Runnable shutdownHook, Priority priority) {
        if (shutdownHook == null) {
            throw new IllegalArgumentException("shutdownHook cannot be NULL");
        }
        if (shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot add a shutdownHook");
        }
        hooks.add(new HookEntry(shutdownHook, priority));
    }

    public boolean removeShutdownHook(Runnable shutdownHook) {
        if (shutdownInProgress.get()) {
            throw new IllegalStateException("Shutdown in progress, cannot remove a shutdownHook");
        }
        return hooks.remove(new HookEntry(shutdownHook, Priority.HIGH));
    }

    public boolean hasShutdownHook(Runnable shutdownHook, Priority priority) {
        return hooks.contains(new HookEntry(shutdownHook, priority));
    }

    public boolean isShutdownInProgress() {
        return shutdownInProgress.get();
    }

    public void removeAllShutdownHook() {
        hooks.clear();
    }

}
