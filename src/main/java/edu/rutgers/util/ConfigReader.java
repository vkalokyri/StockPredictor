package edu.rutgers.util;

import java.util.Properties;

import edu.rutgers.util.enums.PROPERTIES;

/**
 * Singleton class which provides cross-app access to app.properties configuration variables.
 */
public class ConfigReader {
	
	private final static String gPropsFile = "app.properties";
	private static Properties gProperties;
	private volatile static ConfigReader instance;
	
	private ConfigReader() {
		gProperties = new Properties();
		try {
			gProperties.load(ConfigReader.class.getClassLoader().getResourceAsStream(ConfigReader.gPropsFile));
		} catch (Exception e) {
			System.out.println("FATAL ERROR: Initializing application's properties");
		}
	}
	
	/**
	 * Returns a singleton instance of the ConfigReader.
	 * @return
	 */
	public synchronized static ConfigReader getInstance() {
		if(ConfigReader.instance == null) {
			ConfigReader.instance = new ConfigReader();
		}
		return ConfigReader.instance;
	}
	
	/**
	 * Retrieves a String value of a property.
	 * @return String value of property
	 */
	public String getStr(final PROPERTIES penmProp) {
		return gProperties.getProperty(penmProp.toString());
	}
	
	public boolean getBool(final PROPERTIES penmProp) {
		String strVal = gProperties.getProperty(penmProp.toString());
		return strVal.equalsIgnoreCase("true") || strVal.equals("1");
	}
}
