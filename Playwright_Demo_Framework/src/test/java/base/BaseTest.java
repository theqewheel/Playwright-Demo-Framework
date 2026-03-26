package base;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

import org.slf4j.MDC;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Tracing;

import framework.config.ConfigManager;
import framework.drivers.DriverManager;
import framework.listeners.TestListener;
import framework.logging.LogManager;
import framework.logging.UniversalLogger;
import pages.ae.CartPage;
import pages.ae.CheckOutPage;
import pages.ae.ContactUsPage;
import pages.ae.HomePage;
import pages.ae.ProductDetailPage;
import pages.ae.ProductsPage;
import pages.ae.SignupDetailPage;
import pages.ae.SignupLoginPage;
import pages.ae.TestCasesPage;
import reporting.ReportManager;

@Listeners(TestListener.class)
public class BaseTest {

	protected Page page;
	protected UniversalLogger logger;
	protected SoftAssert softAssert;
	protected DriverManager driver;
	protected HomePage homePage;
	protected SignupLoginPage signupLoginPage;
	protected ContactUsPage contactUsPage;
	protected ProductsPage productsPage;
	protected SignupDetailPage signupDetailPage;
	protected TestCasesPage testCasesPage;
	protected CartPage cartPage;
	protected CheckOutPage checkOutPage;
	protected ProductDetailPage productDetailPage;

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * SETUP - RUN Before every test method
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	@BeforeMethod
	public void setup(Method method, ITestContext context) {

		logger = LogManager.getLogger(this.getClass());
		driver = new DriverManager();
		softAssert = new SoftAssert();

		// ✅ Add browser to the method name to make log file unique per browsers run
		// across threads if exists
		String browser = context.getCurrentXmlTest().getParameter("bs.browser");
		if (browser == null || browser.isEmpty()) {
			browser = ConfigManager.getBrowser() != null ? ConfigManager.getBrowser() : "chrome";
		}

		// ✅ Build test name here — available immediately from Method parameter
		// This is the SAME value onTestStart will set in MDC shortly after
		String testName = this.getClass().getSimpleName() + "#" + method.getName();

		// ✅ Read bs.browser from TestNG context — works for all modes
		// For BS runs: comes from <parameter name="bs.browser" value="chrome"/>
		// For local/github: returns null → ignored safely
		if ("browserstack".equals(ConfigManager.getExecutionMode())) {
			DriverManager.setBsBrowser(browser);
			DriverManager.setBsTestName(testName); // ✅ NEW — pass name before init
			logger.info("BrowserStack browser: {}, test: {}", browser, testName);
		}

		driver.initDriver();

		page = driver.getPage();

		logger.info("Initial SETUP completed.");

		// ── Initialize page objects ───────────────────────────────────────────
		homePage = new HomePage(page, softAssert);
		signupLoginPage = new SignupLoginPage(page, softAssert);
		contactUsPage = new ContactUsPage(page, softAssert);
		productsPage = new ProductsPage(page, softAssert);
		signupDetailPage = new SignupDetailPage(page, softAssert);
		testCasesPage = new TestCasesPage(page, softAssert);
		cartPage = new CartPage(page, softAssert);
		checkOutPage = new CheckOutPage(page, softAssert);
		productDetailPage = new ProductDetailPage(page, softAssert);
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * TEARDOWN - RUN After every test method
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	@AfterMethod
	public void teardown(ITestResult result) {

		// ── 1. Soft assert check ──────────────────────────────────────────────
		AssertionError softAssertError = null;
		try {
			softAssert.assertAll();
		} catch (AssertionError e) {
			softAssertError = e;
			result.setStatus(ITestResult.FAILURE);
			result.setThrowable(e);
			logger.error("Soft assertion failure in: {}", result.getName());
		}

		// ── 2. Capture everything BEFORE driver closes ────────────────────────
		Path tracePath = null;
		Path videoPath = null;

		try {
			// ── Stop trace ────────────────────────────────────────────────────
			tracePath = Files.createTempFile("trace-" + result.getName() + "-", ".zip");
			driver.getContext().tracing().stop(new Tracing.StopOptions().setPath(tracePath));
			logger.info("Trace stopped for: {}", result.getName());
		} catch (Exception e) {
			logger.error("Failed to stop trace: {}", e.getMessage());
		}

		try {
			// ── Get video path — while page still open ────────────────────────
			if (page.video() != null) {
				videoPath = page.video().path();
			}
		} catch (Exception e) {
			logger.warn("Could not get video path: {}", e.getMessage());
		}

		// ── 3. Attach after driver closes — finally guarantees driver always closes
		try {
			// Nothing here — attachments happen in finally AFTER driver closes
		} finally {

			// ── Step 1: Mark BS status FIRST — page must still be open ──────────
			markTestStatusBrowserStack(result);

			// ── Step 2: Close driver ─────────────────────────────────────────────
			try {
				driver.quitDriver();
				logger.info("Driver shutdown successful.");
			} catch (Exception e) {
				logger.error("Error during driver shutdown: {}", e.getMessage());
			}

			// ── Step 3: Attach trace ─────────────────────────────────────────────
			try {
				if (tracePath != null && Files.exists(tracePath)) {
					ReportManager.addFileAttachement("🎭 Playwright Trace — " + result.getName(), "application/zip",
							tracePath, ".zip");
					ReportManager.logStep("Trace captured. Download and open at https://trace.playwright.dev");
					logger.info("Trace attached for: {}", result.getName());
				}
			} catch (Exception e) {
				logger.error("Failed to attach trace: {}", e.getMessage());
			} finally {
				deleteTempFile(tracePath, "trace");
			}

			// ── Step 4: Attach video ─────────────────────────────────────────────
			try {
				if (videoPath != null && Files.exists(videoPath)) {
					ReportManager.addFileAttachement("🎬 Test Video — " + result.getName(), "video/webm", videoPath,
							".webm");
					logger.info("Video attached for: {}", result.getName());
				}
			} catch (Exception e) {
				logger.error("Failed to attach video: {}", e.getMessage());
			} finally {
				deleteTempFile(videoPath, "video");
				driver.cleanVideoDir();
			}

			// ── Step 5: Attach log to Allure ─────────────────────────────────────
			try {
				Path logPath = getLogFilePath(MDC.get("methodname"));
				if (logPath != null && Files.exists(logPath)) {
					ReportManager.addFileAttachement("📋 Test Log — " + MDC.get("methodname"), "text/plain", logPath,
							".log");
					logger.info("Log file attached for: {}", MDC.get("methodname"));
				}
			} catch (Exception e) {
				logger.error("Failed to attach log file: {}", e.getMessage());
			}

			// ── Step 6: Upload terminal logs to BrowserStack ─────────────────────
			// ✅ LAST step — all log lines are now written, driver is closed
			// This gives Logback time to flush all pending writes before we read the file
			try {
				if ("browserstack".equalsIgnoreCase(ConfigManager.getExecutionMode())) {
					driver.uploadTerminalLogsToBrowserStack(MDC.get("methodname"));
					logger.info("Terminal logs uploaded to BrowserStack.");
				}
			} catch (Exception e) {
				logger.error("Failed to upload terminal logs to BS: {}", e.getMessage());
			}

		}

		// ── 4. Mark failures on Soft Assertions so TestNG marks test correctly
		// ─────────────────
		if (softAssertError != null) {
			result.setStatus(ITestResult.FAILURE);
			result.setThrowable(softAssertError);
		}

	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * HELPERS
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	// ── Manual screenshot for use inside test methods ─────────────────────────
	protected void captureScreenshot(String stepDescription) {
		try {
			byte[] screenshot = page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
			ReportManager.attachScreenshot("📸 Screenshot - " + stepDescription, screenshot);
		} catch (Exception e) {
			logger.error("Manual screenshot failed: {}", e.getMessage());
		}
	}

	// ── Get per-test log file path ─────────────────────────────────────────────
	// SiftingAppender writes one file per test to logs/tests/{testname}.log
	private Path getLogFilePath(String testName) {
		try {
			Path logFile = Path.of("logs/tests/" + testName + ".log");
			if (Files.exists(logFile)) {
				return logFile;
			} else {
				logger.warn("Log file not found for test: {}", testName);
			}
		} catch (Exception e) {
			logger.warn("Could not locate log file: {}", e.getMessage());
		}
		return null;
	}

	// ── Delete temp file safely ────────────────────────────────────────────────
	private void deleteTempFile(Path path, String type) {
		if (path != null) {
			try {
				Files.deleteIfExists(path);
				logger.info("{} temp file deleted from disk.", type);
			} catch (IOException e) {
				logger.warn("Could not delete {} temp file: {}", type, e.getMessage());
			}
		}
	}

	// Mark browser stack session with correct status of test
	private void markTestStatusBrowserStack(ITestResult result) {
		if (!"browserstack".equalsIgnoreCase(ConfigManager.getExecutionMode()))
			return;

		try {
			String sessionId = DriverManager.getBsSessionId();
			if (sessionId == null || sessionId.isEmpty()) {
				logger.warn("No BS session ID — cannot mark status");
				return;
			}

			String status = switch (result.getStatus()) {
			case ITestResult.SUCCESS -> "passed";
			case ITestResult.FAILURE -> "failed";
			case ITestResult.SKIP -> "failed";
			default -> "failed";
			};

			String reason = result.getThrowable() != null ? result.getThrowable().getMessage() : "Test " + status;

			if (reason != null) {
				reason = reason.replace("\"", "'").replace("\n", " ").replace("\r", " ");
				if (reason.length() > 200)
					reason = reason.substring(0, 200) + "...";
			} else {
				reason = "No reason available";
			}

			String username = ConfigManager.getBSUsername();
			String accessKey = ConfigManager.getBSAccessKey();
			String credentials = Base64.getEncoder().encodeToString((username + ":" + accessKey).getBytes());

			String body = "{\"status\":\"" + status + "\"," + "\"reason\":\"" + reason + "\"}";

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create("https://api.browserstack.com/automate/sessions/" + sessionId + ".json"))
					.header("Authorization", "Basic " + credentials).header("Content-Type", "application/json")
					.PUT(HttpRequest.BodyPublishers.ofString(body)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			logger.info("BS status marked: {} — response: {}", status, response.statusCode());

		} catch (Exception e) {
			logger.warn("Could not mark BS session status: {}", e.getMessage());
		}
	}

}