#!/bin/sh
#################################################
#     Author: yjfei, Date: 2013/03/27           #
#            Environment configuration          #
#################################################

# set jvm startup argument
JAVA_OPTS="-Xms3g \
            -Xmx3g \
            -Xmn2g \
            -XX:PermSize=128m \
            -XX:MaxPermSize=128m \
            -XX:-DisableExplicitGC \
            -Djava.awt.headless=true \
            -Dcom.sun.management.jmxremote.port=8801 \
            -Dcom.sun.management.jmxremote.authenticate=false \
            -Dcom.sun.management.jmxremote.ssl=false \
            -Dfile.encoding=utf-8 \
            -XX:+PrintGC \
            -XX:+PrintGCDetails \
            -XX:+PrintGCDateStamps \
            -XX:-OmitStackTraceInFastThrow \
            -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/home/op1/logging/dump/collector \
            -Xdebug -Xrunjdwp:transport=dt_socket,address=8901,server=y,suspend=n \
            "
export JAVA_OPTS=${JAVA_OPTS}