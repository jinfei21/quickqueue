package com.ctrip.quickqueue;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.glassfish.grizzly.http.util.Header;

import com.ctrip.quickqueue.util.ByteUtils;
import com.ctrip.quickqueue.util.HeaderUtils;

public class send {
	
	
	public static void main(String args[]) throws URISyntaxException, HttpException, IOException{
		
		
		QueueAgent agent=QueueAgent.getAgent("http://127.0.0.1:63200/chunks/");
		
		String content = "fdsafsadfasfsadfsafsdaddddddddddddddddddddddddddddddddddddddddddddddddddddrrrrrrrrrrrrrrrrrrr444444444444444444444444444444444444444444"
				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"				+ "444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444hhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh"
				+ "hhhhhhhhhhhhhhhhhhhhhhhhfffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff";
		while(true){
			agent.put("pv", content);
		}
	}

}
