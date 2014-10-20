package com.ctrip.quickqueue.rest.resource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.ctrip.quickqueue.constant.Constant;
import com.ctrip.quickqueue.constant.Header;
import com.ctrip.quickqueue.factory.RequestBuilder;
import com.ctrip.quickqueue.intf.IRequest;
import com.ctrip.quickqueue.intf.IResponse;
import com.ctrip.quickqueue.rest.server.QuickQueueServer;

@Path("/chunks")
public class QuickQueueChunk {
	
	
	

    @PUT
    @Consumes(MediaType.APPLICATION_OCTET_STREAM)
    public void putChunk(InputStream input) throws IOException {
		IRequest request = RequestBuilder.buildRequest();
		
		request.addHeader(Header.COMMAND.getCode(), Constant.PRODUCE_CHUNK);
		byte[] data = readStream(input);
		request.setBody(data);
		
		IResponse response = QuickQueueServer.APPLICATION.handle(request);
	}
    
    @Path("/config")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getConfig() {
    	IRequest request = RequestBuilder.buildRequest();
    	request.addHeader(Header.COMMAND.getCode(), Constant.GET_CONFIG);
    	IResponse response = QuickQueueServer.APPLICATION.handle(request);
    	return response.getBody().toString();
    }
	
    @Path("/{queueName}")
    @GET
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public byte[] getChunk(@PathParam("queueName") String queueName) {
    	
        IRequest request = RequestBuilder.buildRequest();
        request.addHeader(Header.COMMAND.getCode(), Constant.CONSUME_CHUNK);
        request.setBody(queueName);
        IResponse response = QuickQueueServer.APPLICATION.handle(request);
        return (byte[]) response.getBody();

    }
    
    
    private byte[] readStream(InputStream input) throws IOException{
    	ByteArrayOutputStream buf =  new ByteArrayOutputStream();
    	int len = 2048;
    	byte tmp[] = new byte[len];
    	while(input.available() > 0){
    		len = input.read(tmp);
    		buf.write(tmp, 0, len);
    	}
    	
    	return buf.toByteArray();
    }
    
}
