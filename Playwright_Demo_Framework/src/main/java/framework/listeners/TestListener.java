package framework.listeners;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.microsoft.playwright.Page;

import framework.config.ConfigManager;
import framework.drivers.DriverManager;
import framework.logging.LogManager;
import reporting.ReportManager;

public class TestListener implements ITestListener, ISuiteListener {
	
	private static final Logger logger = LogManager.getLogger(TestListener.class);
	
	/**
	 * ======================= SUITE LEVEL LISTENERS ======================
	 */
	
	/*
	 * This method is invoked before the SuiteRunner begins to run any tests in the suite.
	 */
	
	@Override
	public void onStart(ISuite suite) {
		
		ConfigManager.initializeEnvironment(suite);
		
		logger.info("================XXXXXXXXXXXXXX================");
		logger.info("Starting Suite: {}",suite.getName());
		logger.info("Execution Environment: {}",ConfigManager.getEnvironment());
		logger.info("==============================================");
	}
	
	/*
	 * This method is invoked after the SuiteRunner completes run of all tests in the suite.
	 */
	
	
	@Override
	public void onFinish(ISuite suite) {
		
		logger.info("==============================================");
		logger.info("Finished Suite: {}",suite.getName());
		logger.info("================XXXXXXXXXXXXXX================");
	}
	
	/**
	 * ======================= TEST LEVEL LISTENERS ======================
	 */
	
	/*
	 * This method is invoked before any Test begins in the suite.
	 */
	
	public void onTestStart(ITestResult result) {
		
		String testName = result.getMethod().getMethodName();
		
		//Set MDC value for the logging pattern
		MDC.put("env", ConfigManager.getEnvironment());
		MDC.put("testname",testName);
		
		logger.info("-----------------  STARTING TEST : {}  -----------------" , testName);
	}
	
	/*
	 * This method is invoked when any Test succeeds in the suite.
	 */
	
	public void onTestSuccess(ITestResult result) {
		
		String testName = result.getMethod().getMethodName();
		
		logger.info("{TEST PASSED: {}", testName);
		
		if(ConfigManager.isScreenhotonPass()) {
			
			attachScreenshot("PASSED Screenshot:", result);
		}
		
		MDC.clear();
		
	}
	
	/*
	 * This method is invoked when any Test succeeds in the suite.
	 */
	
	public void onTestFailure(ITestResult result) {
		
		String testName = result.getMethod().getMethodName();
		
		logger.info("{TEST FAILED: {}", testName);
		
		if(result.getThrowable() != null) {
			logger.error("Failure Reason: " + result.getThrowable());
		}
		
		if(ConfigManager.isScreenhotonFail()) {
			
			attachScreenshot("FAILURE Screenshot:", result);
		}
		
		MDC.clear();
		
	}
	
	/*
	 * This method is invoked when any Test succeeds in the suite.
	 */
	
	public void onTestSkipped(ITestResult result) {
		
		String testName = result.getMethod().getMethodName();
		
		logger.info("{TEST SKIPPED: {}", testName);
		
		if(result.getThrowable() != null) {
			logger.error("Skip Reason: " + result.getThrowable());
		}
		
		if(ConfigManager.isScreenhotonFail()) {
			
			//capture screenshot
		}
		
		MDC.clear();
		
	}
	
	
	/**
	 * ======================= HELPER METHODS ======================
	 */
	
	private void attachScreenshot(String name, ITestResult result) {
		
		Page page = DriverManager.getPage();
		
		if(page != null) {
			
			//capture screenshot with Playwright
			
			byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
			
			//attach screenshot with Report Manager
			
			ReportManager.attachScreenshot(name, screenshot);
			
			if(result.getStatus() == ITestResult.FAILURE) {
				ReportManager.logStep("Test failed: {}"+ result.getThrowable().getMessage());
			}
		}
	}
	
}
