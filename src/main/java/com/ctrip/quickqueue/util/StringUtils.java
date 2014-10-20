package com.ctrip.quickqueue.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class StringUtils {
	
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static String getStackTrace(Throwable t) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        t.printStackTrace(pw);
        return sw.toString(); // stack trace as a string
    }
    
    
    
}
