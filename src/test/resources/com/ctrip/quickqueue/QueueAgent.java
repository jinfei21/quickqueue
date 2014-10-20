package com.ctrip.quickqueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.ws.rs.core.MediaType;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParamConfig;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpParamsNames;

public class QueueAgent {
	private HttpClient client;
	private String endpoint;
	private static QueueAgent agent;
	private QueueAgent(String endpoint){
		this.client = new DefaultHttpClient();
		
		this.endpoint = endpoint;
	}
	
	public static QueueAgent getAgent(String endpoint){
		if(agent == null){
			synchronized (QueueAgent.class) {
				if(agent == null){
					agent = new QueueAgent(endpoint);
				}
			}
		}
		return agent;
	}

	
	
	public void put(String route,String content){
		HttpPut put = new HttpPut(endpoint);
		
		QueueMessage msg = new QueueMessage(route, content);
		try {
			put.setEntity(new ByteArrayEntity(msg.toBytes()));
			put.addHeader(HttpHeaders.CONNECTION, "keep-alive");
			put.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM);
			HttpResponse  response = client.execute(put);
			if (response != null) {
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode > 300) {
					System.out.println("fail!");
				}
				
			}
			if(put.getEntity() != null){
				put.getEntity().getContent().close();
			}
		} catch (IOException e) {
			return ;
		}
		
		
		
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
	
	public QueueMessage get(String queue){
		HttpGet get = new HttpGet(endpoint+queue);
		try {
			HttpResponse response = client.execute(get);
			
			if (response != null) {
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200 || statusCode == 403) {
					InputStream input = response.getEntity().getContent();
					
					byte[] buf = readStream(input);
					
					QueueMessage msg = new QueueMessage(buf);
					input.close();
					return msg;
				}
			}

			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return null;
		}
		
		return null;
	}
}
