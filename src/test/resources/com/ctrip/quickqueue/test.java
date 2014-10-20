package com.ctrip.quickqueue;

import java.util.LinkedList;
import java.util.List;



public  class test {
	
	public static synchronized void A(){
		System.out.println("A");
		B();
	}
	

	public static synchronized void B(){
		System.out.println("B");
	}

	public static void main(String[] args) {
		
		A();
		test t =new test();
		List<String> l = new LinkedList<String>();
		
		System.gc();
		System.gc();
		System.gc();
		System.gc();System.gc();

	}

}
