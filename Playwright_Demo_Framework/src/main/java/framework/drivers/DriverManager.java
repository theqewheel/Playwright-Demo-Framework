package framework.drivers;

import java.util.List;

import org.slf4j.Logger;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import framework.config.ConfigManager;
import framework.logging.LogManager;

public class DriverManager {
	
	private static Playwright playwright;
	private static Browser browser;
	private static BrowserContext context;
	private static Page page;
	private static final Logger logger = LogManager.getLogger(DriverManager.class);
	private static boolean headlessMode;
	
	public static void initDriver() {
		
		headlessMode = Boolean.parseBoolean(ConfigManager.getProperty("headless"));
		
		String baseURL = ConfigManager.getBaseURL();
		
		try {
				logger.info("Initiating Driver . . . . . ");
				
				playwright = Playwright.create();
				
				playwright.selectors().setTestIdAttribute(ConfigManager.getProperty("test-id"));
				
				browser = initBrowser();
				
				logger.info("Launching Browser. HeadlessMode: {}", String.valueOf(headlessMode));
				
				context = browser.newContext(new Browser.NewContextOptions().setViewportSize(null));
				
				logger.info("Browser Context Launched");
				
				page = context.newPage();
				
				logger.info("Browser Page opened.");
				
				page.navigate(baseURL);
				
				logger.info("Navigated to Base URL: {}", baseURL);
			
		}catch (Exception e){
			
			logger.error("Exception during the driver initiation", e);
			
			throw new RuntimeException("Driver initialization failed",e);
		}
	
		
	}
	
	private static Browser initBrowser() {
		
		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions()
				.setHeadless(headlessMode)
				.setSlowMo(1000)
				.setArgs(List.of("--start-maximized"));
		
		switch (ConfigManager.getBrowser()) {
			
			case "firefox":
				return playwright.firefox().launch(options);
				
			case "webkit":
				return playwright.webkit().launch(options);
				
			case "edge":
				return playwright.chromium().launch(options.setChannel("msedge"));
				
			case "chrome":
				return playwright.chromium().launch(options.setChannel("chrome"));
			
			case "chromium":
			default:
				return playwright.chromium().launch(options);
				
		}
	}
	
	public static void quitDriver() {
		
		if(context != null) context.close();
		if(browser != null) browser.close();
		if(playwright != null) playwright.close();
		
		logger.info("Closed Browser !!");
	}
	
	public static Page getPage() {
		return page;
	}
	
}
