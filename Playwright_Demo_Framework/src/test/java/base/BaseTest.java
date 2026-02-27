package base;

import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

import framework.config.ConfigManager;
import framework.drivers.DriverManager;
import framework.logging.LogManager;

public class BaseTest {
	
	protected Page page;
	protected Logger logger;
	
	@BeforeMethod
	public void setup() {
		
		logger = LogManager.getLogger(this.getClass());
		
		DriverManager.initDriver();
		
		page = DriverManager.getPage();
		
		logger.info("Initial SETUP is completed.");

	}

	@AfterMethod
	public void teardown() {
		
		DriverManager.quitDriver();
		
		logger.info("Driver SHUTDOWN is successfull !");
	}
	
	protected byte[] captureScreenshot() {
		return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
	}

}
