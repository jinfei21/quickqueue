package com.ctrip.quickqueue.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.ctrip.quickqueue.util.StringUtils;


public class QuickQueueAppender extends AppenderBase<ILoggingEvent> {

    private String appId;
    private String serverIp;
    private int serverPort;

    @Override
    public void start() {
        int errors = 0;
        if (StringUtils.isEmpty(appId)) {
            errors++;
            addError("\"appId\" property not set for appender named [" + name + "].");
        }
        if (StringUtils.isEmpty(serverIp)) {
            errors++;
            addError("\"serverIp\" property not set for appender named [" + name + "].");
        }
        if (errors > 0) {
            return;
        }

        addInfo("appId property is set to [" + appId + "]");
        addInfo("serverIp property is set to [" + serverIp + "]");
        addInfo("serverPort property is set to [" + serverPort + "]");
        


        System.out.println(appId);
        System.out.println(serverIp);
        System.out.println(String.valueOf(serverPort));
        super.start();
    }

    protected void append(ILoggingEvent event) {
        int level = event.getLevel().levelInt;
        if (level == Level.OFF_INT) {
            return;
        }
        System.out.println(event.getMessage());
    }


    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }
}