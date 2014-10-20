package com.ctrip.quickqueue.util;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctrip.quickqueue.constant.Constant;

public class Configuration {
	private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);
	
	private static Map<String, Properties> props = null;

	private static class ConfigurationHelper {
		
		private static Map<String, Properties> configMap = new HashMap<String, Properties>();
		
		
		public static synchronized void loadConfig(String path){
			
			InputStream in = IOStream.getResourceAsStream(path);
			try{
				Properties prop = new Properties();
				prop.load(in);
				configMap.put(path, prop);
				if(in != null){
					in.close();
				}
			}catch(Exception e){
				LOGGER.warn("load config fail:"+path, e);
			}
		}

		
	}

	public static void addResource(String path) {
		
		ConfigurationHelper.loadConfig(path);

	}

	public static String get(String key) {
		return get(Constant.QUEUE_CONFIG, key);
	}
	
	public static String get(String path,String key){
		Properties p = getProperties(path);
		if( p != null){
			return p.getProperty(key);
		}else{
			return null;
		}
		
	}
	
	public static Properties getProperties(String path){
		Properties p = null;
		if(props == null){
			synchronized (Configuration.class) {
				if(props == null){
					props = ConfigurationHelper.configMap;
				}
			}
		}

		if(props.get(path) == null){
			ConfigurationHelper.loadConfig(path);
		}
		
		return props.get(path);
	}
	
    public static int getInt(String name, int defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        return Integer.parseInt(valueString);
    }

    public static int getInt(String name) {
        return Integer.parseInt(get(name));
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        String valueString = get(name);
        if (valueString == null) {
            return defaultValue;
        }
        return Boolean.valueOf(valueString);
    }

    public static boolean getBoolean(String name) {
        return Boolean.valueOf(get(name));
    }
}
