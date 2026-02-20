package framework.config;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.testng.ISuite;

import framework.logging.LogManager;


public class ConfigManager {
	
	private static Properties properties = new Properties();
	protected static final Logger logger = LogManager.getLogger(ConfigManager.class);
	private static String environment;
	
	static {
		try {
			InputStream input = ConfigManager.class
					.getClassLoader()
					.getResourceAsStream("config.properties");
			
			if(input == null) {
				logger.error("Runtime Exception: ",  new RuntimeException("config.properties file not found in classpath"));
			}
			
			properties.load(input);
			logAllProperties();
		}
		catch (Exception e) {
			logger.error("Error loading config.properties file: " + e.getMessage());
			e.printStackTrace();
		}
	
	}
	
	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static String getBaseURL() {	
		return getProperty(getEnvironment() + ".base.url");
	}
	
	public static boolean isScreenhotonPass() {
		return Boolean.parseBoolean(getProperty("screenshot.on.pass"));
	}
	
	public static boolean isScreenhotonFail() {
		return Boolean.parseBoolean(getProperty("screenshot.on.fail"));
	}
	
	public static String getBrowser() {
		
		//System property - first priority (CLI or CI)
		String browser = System.getProperty("browser"); 
		
		if(browser == null || browser.isEmpty()) {
			browser = properties.getProperty("browser", "chromium"); //Default to chromium if not specified
		}
		
		return browser.toLowerCase();
	}
	
	public static void logAllProperties() {
		properties.forEach((key, value) -> {
			logger.debug(key + " = " + value);
		});
		
	}
	
	//Called once for the Listener
	public static void initializeEnvironment(ISuite suite) {
		
		//System property - first priority (CLI or CI)
		String env = System.getProperty("environment");
		
		//TestNG XML parameter - second priority
		if(env == null || env.isEmpty()) {
			env = suite.getParameter("environment");
		}
		
		//Config file property - third priority
		if(env == null || env.isEmpty()) {
			env = getProperty("env");
		}
		
		//Default to 'qa' if no environment is specified
		if(env == null || env.isEmpty()) {
			env = "qa";
			logger.warn("No environment specified. Defaulting to 'qa'");
		}
		
		environment = env;
		
		//Optional for using unified environment variable in logs
		System.setProperty("environment", env);
		
	}
	
	//Called from Test Listener
	public static String getEnvironment() {
		
		if(environment == null || environment.isEmpty()) {
			logger.warn("Environment not initialized. Defaulting to 'qa'");
			environment = "qa";
		}
		
		return environment.toLowerCase();
	}
		

}
