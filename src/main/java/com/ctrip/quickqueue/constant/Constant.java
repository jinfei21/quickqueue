package com.ctrip.quickqueue.constant;

public class Constant {

	//Command
    public final static String PRODUCE_CHUNK = "produce_chunk";
    public final static String CONSUME_CHUNK = "consume_chunk";
    public final static String GET_CONFIG = "get_config";
    
    //Config Path
    public final static String QUEUE_CONFIG = "quickqueue.properties";
    
    
    //Param
    public static final String QUICKQUEUE_SCAN_PACKAGE = "quickqueue.scan.package";
    public static final String QUICKQUEUE_OBJECT_FACTORY = "quickqueue.object.factory";
    public static final String QUICKQUEUE_REST_RESOURCE = "quickqueue.resource";
    public static final String QUICKQUEUE_REST_PORT = "quickqueue.port";
    public static final String QUICKQUEUE_DISKQUEUE_MAXSIZE = "quickqueue.diskqueue.maxsize";
    public static final String QUICKQUEUE_MEMQUEUE_MAXCHUNKCOUNT = "quickqueue.memqueue.maxchunkcount";
    public static final String QUICKQUEUE_DISKQUEUE_DIR = "quickqueue.diskqueue.dir";
    public static final String QUICKQUEUE_NAMES = "quickqueue.names";
    public static final String QUICKQUEUE_ROUTE_PREFIX = "quickqueue.route.";
}
