package com.ctrip.quickqueue.rest.server;

import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.grizzly.http.server.HttpServer;

import com.ctrip.quickqueue.intf.AbstractServer;
import com.ctrip.quickqueue.intf.IApplication;
import com.sun.jersey.api.container.grizzly2.GrizzlyServerFactory;
import com.sun.jersey.api.core.PackagesResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;

public class QuickQueueServer extends AbstractServer {
	
	public static IApplication APPLICATION;
	private final String packages;
	private final int port;
	
	private HttpServer httpServer;
	
	public QuickQueueServer(String packages,int port,IApplication application){
		this.packages = packages;
		this.port = port;
		QuickQueueServer.APPLICATION = application;
		
	}

	@Override
	public void doStartup() throws Exception {
		
		URI uri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
        ResourceConfig rc = new PackagesResourceConfig(packages, "org.codehaus.jackson.jaxrs","com.sun.jersey.api.container.filter");

        try {
            System.out.println("Start the rest server ...");
            httpServer = GrizzlyServerFactory.createHttpServer(uri, rc);
            System.out.println("Started the rest server succeed");
        } catch (Throwable e) {
            throw new Exception("Start restful collector error: ", e);
        }
	}

	@Override
	public void doShutdown() throws Exception {
        System.out.println("Stop  ...");

        if (httpServer != null && httpServer.isStarted()) httpServer.stop();

        System.out.println("Stopped.");
	}

}
