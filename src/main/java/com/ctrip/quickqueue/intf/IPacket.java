package com.ctrip.quickqueue.intf;

import java.util.Map;

public interface IPacket <T> {
    Map<String, String> getHeaders();

    void setHeaders(Map<String,String> headers);

    T getBody();

    void setBody(T body);

    String getHeader(String headerName);

    void addHeader(String headerName, String headerValue);

    String getHost();

    String getIP();

    String getCompress();

    String getSerialize();

    String getCommand();

    String getRoute();

    String getAppId();
}