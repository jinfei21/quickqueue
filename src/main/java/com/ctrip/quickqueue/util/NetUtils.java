package com.ctrip.quickqueue.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class NetUtils {
	
	
    public static String getHostNameAndIP() {
        try {
            return InetAddress.getLocalHost().toString();
        } catch (UnknownHostException e) {
            return "-NA-";
        }
    }
    
    public static String getHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "-NA-";
        }
    }
    
}
