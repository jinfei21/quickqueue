package com.ctrip.quickqueue.util;

public class IPUtils {

    public static boolean isValidIP(String ip){
        try {
            if (ip == null || ip == ""){
                return false;
            }

            for (int i=0;i<ip.length();i++){
                if (ip.charAt(i)<'0' || ip.charAt(i) > '9'){
                    if (ip.charAt(i) != '.'){
                        return false;
                    }
                }
            }

            int length = ip.split("\\.").length;
            if (length != 4){
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
