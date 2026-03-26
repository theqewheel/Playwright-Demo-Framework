package framework.listeners;

import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.MDC;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.microsoft.playwright.Page;

import framework.config.ConfigManager;
import framework.drivers.DriverManager;
import framework.logging.LogManager;
import framework.logging.UniversalLogger;
import io.qameta.allure.Allure;
import reporting.ReportManager;

public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {

	private static final UniversalLogger logger = LogManager.getLogger(TestListener.class);

	/**
	 * ======================= SUITE LEVEL LISTENERS ======================
	 */

	/*
	 * This method is invoked before the SuiteRunner begins to run any tests in the
	 * suite.
	 */

	@Override
	public void onStart(ISuite suite) {

		ConfigManager.initializeEnvironment(suite);

		logger.info("================XXXXXXXXXXXXXX================");
		logger.info("Starting Suite: {}", suite.getName());
		logger.info("Execution Environment: {}", ConfigManager.getEnvironment());
		logger.info("==============================================");
	}

	/*
	 * This method is invoked after the SuiteRunner completes run of all tests in
	 * the suite.
	 */

	@Override
	public void onFinish(ISuite suite) {

		logger.info("==============================================");
		logger.info("Finished Suite: {}", suite.getName());
		logger.info("================XXXXXXXXXXXXXX================");

		// ✅ Attach suite-level log to Allure
		attachSuiteLog();
	}

	/**
	 * ======================= TEST LEVEL LISTENERS ======================
	 */

	/*
	 * This method is invoked before any Test begins in the suite.
	 */

	public void onTestStart(ITestResult result) {

		String className = result.getTestClass().getRealClass().getSimpleName();
		String methodName = result.getMethod().getMethodName();

		String browser = result.getTestContext().getCurrentXmlTest().getParameter("bs.browser");

		if (browser == null || browser.isEmpty()) {
			browser = ConfigManager.getBrowser();
		}
		if (browser == null || browser.isEmpty()) {
			browser = "unknown";
		}

		// ✅ Add thread ID — makes log filename unique per thread
		// Prevents SiftingAppender collision when same test runs
		// on different threads (retries, multi-browser, reuse)
		String threadId = String.valueOf(Thread.currentThread().getId());

		// Format: MethodName_browser_threadId
		// e.g. Test_AE2_TC02_chrome_42
		String methodNameWithBrowser = methodName + "_" + browser + "_" + threadId;

		String testName = className + "#" + methodName;

		MDC.put("env", ConfigManager.getEnvironment());
		MDC.put("testname", testName);
		MDC.put("methodname", methodNameWithBrowser);

		logger.info("--- STARTING TEST : {} on {} [thread-{}] ---", testName, browser, threadId);
	}

	/*
	 * This method is invoked when any Test succeeds in the suite.
	 */

	public void onTestSuccess(ITestResult result) {

		// ✅ Re-establish MDC in case this fires on a different thread
		// In parallel runs, listener callbacks may not inherit the test thread's MDC
		ensureMDC(result);

		String testName = result.getMethod().getMethodName();

		logger.info("TEST PASSED: {}", testName);

		if (ConfigManager.isScreenhotonPass()) {

			attachScreenshot("PASSED Screenshot:", result);
		}
	}

	/*
	 * This method is invoked when any Test succeeds in the suite.
	 */

	public void onTestFailure(ITestResult result) {

		// ✅ Re-establish MDC in case this fires on a different thread
		// In parallel runs, listener callbacks may not inherit the test thread's MDC
		ensureMDC(result);

		String testName = result.getMethod().getMethodName();

		logger.info("TEST FAILED: {}", testName);

		if (result.getThrowable() != null) {
			logger.error("Failure Reason: " + result.getThrowable());
		}

		if (ConfigManager.isScreenhotonFail()) {

			attachScreenshot("FAILURE Screenshot:", result);
		}
	}

	/*
	 * This method is invoked when any Test succeeds in the suite.
	 */

	public void onTestSkipped(ITestResult result) {

		// ✅ Re-establish MDC in case this fires on a different thread
		// In parallel runs, listener callbacks may not inherit the test thread's MDC
		ensureMDC(result);

		String testName = result.getMethod().getMethodName();

		logger.info("TEST SKIPPED: {}", testName);

		if (result.getThrowable() != null) {
			logger.error("Skip Reason: " + result.getThrowable());
		}

		if (ConfigManager.isScreenhotonFail()) {

			// capture screenshot
		}
	}

	/*
	 * This method is invoked after any Test Method completes
	 */

	public void afterInvocation(IInvokedMethod method, ITestResult result) {

		// ✅ Block 1 — Actual test failure
		if (method.isTestMethod() && result.getStatus() == ITestResult.FAILURE) {
			Throwable cause = result.getThrowable();

			if (cause != null && !(cause instanceof AssertionError)) {
				logger.error("========================================");
				logger.error("         ACTUAL TEST FAILURE            ");
				logger.error("========================================");
				logger.error("Test    : {}", result.getMethod().getMethodName());
				logger.error("Cause   : {}", cause.getClass().getSimpleName());

				// ✅ Pass cause as throwable - makes stack trace clickable in IDE!
				logger.error("Details : ", cause);
				logger.error("==================XXXXX=================");
			}
		}

		// ✅ Block 2 — Soft assertion failures
		if (method.getTestMethod().isAfterMethodConfiguration()) {
			Throwable throwable = result.getThrowable();

			if (throwable instanceof AssertionError) {
				logger.error("========================================");
				logger.error("       SOFT ASSERTION FAILURES          ");
				logger.error("========================================");

				// Split failures on separate lines
				String[] failures = throwable.getMessage().split(",\n\t");
				for (int i = 0; i < failures.length; i++) {
					logger.error("  Failure {}: {}", i + 1, failures[i].trim());
				}

				// ✅ Pass throwable - makes it clickable too!
				logger.error("Stacktrace : ", throwable);
				logger.error("==================XXXXX=================");

				result.setStatus(ITestResult.FAILURE);
				result.setThrowable(throwable);
			}

			// ✅ ONLY clear MDC after @AfterMethod completes
			// NOT after @BeforeMethod or @Test — those still need MDC for logging
			MDC.clear();
		}
	}

	/**
	 * ======================= HELPER METHODS ======================
	 */

	private void attachScreenshot(String name, ITestResult result) {
		try {
			// ✅ Static getter — gets THIS thread's page from ThreadLocal
			// Never use new DriverManager().getPage() — always returns null!
			Page page = DriverManager.getCurrentPage();

			if (page != null) {
				byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
				ReportManager.attachScreenshot(name, screenshot);

				if (result.getStatus() == ITestResult.FAILURE && result.getThrowable() != null) {
					ReportManager.logStep("Test failed: " + result.getThrowable().getMessage());
				}
				logger.info("Screenshot attached: {}", name);
			} else {
				logger.warn("Page is null — screenshot skipped for: {}", name);
			}
		} catch (Exception e) {
			logger.error("Failed to attach screenshot in listener: {}", e.getMessage());
		}
	}

	private void attachSuiteLog() {
		try {
			Path suiteLog = Path.of("logs/tests/suite-execution.log");
			if (Files.exists(suiteLog)) {
				ReportManager.addFileAttachement("📋 Suite Execution Log", "text/plain", suiteLog, ".log");
				logger.info("Suite log attached to Allure.");
			} else {
				logger.warn("Suite log not found: {}", suiteLog);
			}
		} catch (Exception e) {
			logger.error("Failed to attach suite log: {}", e.getMessage());
		}
	}

	// ── Re-establish MDC if missing — safe for parallel/reporter threads ──────
	private void ensureMDC(ITestResult result) {

		if (MDC.get("methodname") == null || MDC.get("methodname").isEmpty()) {

			String methodName = result.getMethod().getMethodName();

			String browser = result.getTestContext().getCurrentXmlTest().getParameter("bs.browser");

			if (browser == null || browser.isEmpty()) {
				browser = ConfigManager.getBrowser();
			}
			if (browser == null || browser.isEmpty()) {
				browser = "unknown";
			}

			// ✅ Same format as onTestStart — thread ID included
			String threadId = String.valueOf(Thread.currentThread().getId());
			String methodNameWithBrowser = methodName + "_" + browser + "_" + threadId;
			String className = result.getTestClass().getRealClass().getSimpleName();
			String testName = className + "#" + methodName;

			MDC.put("env", ConfigManager.getEnvironment());
			MDC.put("testname", testName);
			MDC.put("methodname", methodNameWithBrowser);

			logger.debug("MDC re-established on listener thread for: {}", methodNameWithBrowser);
		}
	}
}
