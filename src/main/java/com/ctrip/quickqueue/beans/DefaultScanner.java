package com.ctrip.quickqueue.beans;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ctrip.quickqueue.intf.IRequestFilter;
import com.ctrip.quickqueue.intf.IScanner;


public class DefaultScanner implements IScanner {

	private final static Logger LOGGER = LoggerFactory.getLogger(DefaultScanner.class);
	
	public <T> Set<Class<? extends T>> scan(String[] packages, Class<T> clazz) {
		Set<Class<? extends T>> classes = new HashSet<Class<? extends T>>();
		

		for(String p:packages){
			try {
				classes.addAll(scan(p, clazz));
			} catch (ClassNotFoundException e) {
				LOGGER.warn("Scanner fail!", e);
			} catch (IOException e) {
				LOGGER.warn("Scanner fail!", e);
			}
		}
		
		return classes;
	}


	
	public <T> Set<Class<? extends T>> scan(String packageName, Class<T> clazz) throws IOException, ClassNotFoundException{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		String path = packageName.replace('.', '/');
		
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		Set<Class<? extends T>> classes = new HashSet<Class<? extends T>>();
		while(resources.hasMoreElements()){
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		
		for(File dir:dirs){
			if(dir.isDirectory()){
				for(File f:dir.listFiles()){
				    if(f.getName().endsWith(".class")){
				    	Class<? extends T> subClazz = (Class<? extends T>) Class.forName(packageName+'.'+f.getName().substring(0, f.getName().length()-6));
				    	if(clazz.isAssignableFrom(subClazz)){
				    		if(!clazz.equals(subClazz)){
				    			classes.add(subClazz);
				    		}
				    	}
				    }			
				}
			}
		}
		

		return classes;
		
	}

	public static void main(String args[]) throws ClassNotFoundException, IOException {
		DefaultScanner s = new DefaultScanner();
		Set<Class<? extends IRequestFilter>> set = s.scan("com.ctrip.quickqueue.filter",IRequestFilter.class );
		
		System.out.println("fdsafdsa");
	}

}
