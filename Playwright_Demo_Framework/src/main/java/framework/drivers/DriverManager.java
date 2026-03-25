package framework.drivers;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore.PrivateKeyEntry;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.google.gson.Gson;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import framework.config.ConfigManager;
import framework.logging.LogManager;
import io.qameta.allure.Step;

public class DriverManager {

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * ThreadLocal — each thread gets its own instance for PARALLEL runs
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
	private static final ThreadLocal<Browser> browser = new ThreadLocal<>();
	private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
	private static final ThreadLocal<Page> page = new ThreadLocal<>();
	private static final ThreadLocal<Path> videoDir = new ThreadLocal<>();
	private static final ThreadLocal<String> bsBrowserName = new ThreadLocal<>();

	private Logger logger = LogManager.getLogger(DriverManager.class);
	private boolean headlessMode;

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * INIT — entry point, decides local vs BrowserStack
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	public void initDriver() {

		headlessMode = ConfigManager.getHeadlessmode();
		String baseURL = ConfigManager.getBaseURL();
		String mode = ConfigManager.getExecutionMode();

		logger.info("*** Execution mode: {}", mode.toUpperCase());

		try {
			switch (mode) {
			case "browserstack":
				initBrowserStack(baseURL);
				break;
			case "github":
			case "local":
			default:
				initLocalDriver(baseURL);
				break;
			}
		} catch (Exception e) {
			logger.error("Exception during driver initiation", e);
			throw new RuntimeException("Driver initialization failed", e);
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * LOCAL DRIVER — initiation Used for: local runs + GitHub Actions native runs
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	private void initLocalDriver(String baseURL) {

		try {
			logger.info("Initiating Driver . . .");

			playwright.set(Playwright.create());
			playwright.get().selectors().setTestIdAttribute(ConfigManager.getProperty("test-id"));

			browser.set(initLocalBrowser());
			logger.info("Browser launched. HeadlessMode: {}", headlessMode);

			// ✅ Temp dir for video recording
			Path tempVideoDir = Files.createTempDirectory("playwright-videos-");
			videoDir.set(tempVideoDir);

			context.set(browser.get().newContext(new Browser.NewContextOptions().setViewportSize(null)
					.setAcceptDownloads(true).setRecordVideoDir(tempVideoDir).setRecordVideoSize(1280, 720)));

			startTrace(); // ✅ start tracing
			removeAdds(context.get());
			logger.info("Browser Context launched.");

			page.set(context.get().newPage());
			logger.info("Browser Page opened.");

			navigateToAppBaseURL(baseURL);

		} catch (Exception e) {
			logger.error("Exception during LOCAL driver initiation", e);
			throw new RuntimeException("LOCAL Driver initialization failed", e);
		}

	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * LOCAL BROWSER SELECTION
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	private Browser initLocalBrowser() {

		BrowserType.LaunchOptions options = new BrowserType.LaunchOptions().setHeadless(headlessMode).setSlowMo(1000)
				.setArgs(List.of("--start-maximized"));

		switch (ConfigManager.getBrowser()) {
		case "firefox":
			return playwright.get().firefox().launch(options);
		case "webkit":
			return playwright.get().webkit().launch(options);
		case "edge":
			return playwright.get().chromium().launch(options.setChannel("msedge"));
		case "chrome":
			return playwright.get().chromium().launch(options.setChannel("chrome"));
		case "chromium":
		default:
			return playwright.get().chromium().launch(options);
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * BROWSERSTACK DRIVER Connects to BrowserStack cloud via CDP websocket Browser
	 * passed per-thread via -Dbs.browser system property Set in
	 * testng-browserstack.xml as <parameter name="bs.browser"/>
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	private void initBrowserStack(String baseURL) {

		try {

			String username = ConfigManager.getBSUsername();
			String accessKey = ConfigManager.getBSAccessKey();

			// ✅ Read from ThreadLocal — guaranteed correct per thread
			String bsBrowser = bsBrowserName.get();
			if (bsBrowser == null || bsBrowser.isEmpty()) {
				bsBrowser = "chrome"; // safe default
			}

			logger.info("Connecting to BrowserStack. Browser: {}", bsBrowser);

			// ── Build capabilities ─────────────────────────────────────────────
			Map<String, Object> caps = new HashMap<>();
			caps.put("browser", bsBrowser);
			caps.put("browser_version", "latest");
			caps.put("os", "Windows");
			caps.put("os_version", "11");
			caps.put("name", Thread.currentThread().getName());
			caps.put("build", ConfigManager.getBSBuildName());
			caps.put("project", ConfigManager.getBSProjectName());
			caps.put("browserstack.username", username);
			caps.put("browserstack.accessKey", accessKey);
			caps.put("browserstack.debug", "true"); // enables BS screenshots
			caps.put("browserstack.networkLogs", "true"); // enables BS network logs

			// ── Build connection URL ───────────────────────────────────────────
			String capsJson = new Gson().toJson(caps);
			String capsEncoded = URLEncoder.encode(capsJson, StandardCharsets.UTF_8);
			String wsEndpoint = "wss://cdp.browserstack.com/playwright?caps=" + capsEncoded;

			// ── Connect via Playwright CDP ─────────────────────────────────────
			playwright.set(Playwright.create());
			playwright.get().selectors().setTestIdAttribute(ConfigManager.getProperty("test-id"));

			browser.set(playwright.get().chromium().connect(wsEndpoint));

			logger.info("Connected to BrowserStack successfully. Browser: {}", bsBrowser);

			// ── Context — no video on BS (BS records its own video) ───────────
			context.set(browser.get()
					.newContext(new Browser.NewContextOptions().setViewportSize(1280, 720).setAcceptDownloads(true)
					// ✅ No video recording on BS — BS captures its own video
					));

			startTrace();
			removeAdds(context.get());
			logger.info("BrowserStack context launched.");

			page.set(context.get().newPage());
			logger.info("BrowserStack page opened.");

			navigateToAppBaseURL(baseURL);

		} catch (Exception e) {
			logger.error("Exception during BROWSERSTACK driver initiation", e);
			throw new RuntimeException("BROWSERSTACK Driver initialization failed", e);
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * TRACING
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	private void startTrace() {
		try {
			context.get().tracing()
					.start(new Tracing.StartOptions().setScreenshots(true).setSnapshots(true).setSources(true));
			logger.info("Playwright tracing started.");
		} catch (Exception e) {
			logger.warn("Could not start tracing: {}", e.getMessage());
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * QUIT DRIVER
	 * ══════════════════════════════════════════════════════════════════════════
	 */
	public void quitDriver() {
		try {
			if (page.get() != null)
				page.get().close();
			if (context.get() != null)
				context.get().close();
			if (browser.get() != null)
				browser.get().close();
			if (playwright.get() != null)
				playwright.get().close();
			logger.info("Browser closed successfully.");
		} catch (Exception e) {
			logger.error("Error while closing browser: {}", e.getMessage());
		} finally {
			// ✅ Always remove — prevents memory leaks in parallel runs
			page.remove();
			context.remove();
			browser.remove();
			playwright.remove();
			videoDir.remove();
			bsBrowserName.remove();
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * CLEAN-UP VIDEO DIRECTORY
	 * ══════════════════════════════════════════════════════════════════════════
	 */
	public void cleanVideoDir() {
		try {
			Path dir = videoDir.get();
			if (dir != null && Files.exists(dir)) {
				Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
				logger.info("Temp video directory cleaned: {}", dir);
			}
		} catch (Exception e) {
			logger.warn("Could not clean temp video dir: {}", e.getMessage());
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * REMOVE ADVERTISEMENTS - seen for demo sites
	 * ══════════════════════════════════════════════════════════════════════════
	 */
	public void removeAdds(BrowserContext context) {
		context.route("**/*doubleclick.net/**", route -> route.abort());
		context.route("**/*googlesyndication.com/**", route -> route.abort());
		context.route("**/*googleads.g.doubleclick.net/**", route -> route.abort());
		context.route("**/*adservice.google.com/**", route -> route.abort());
		context.route("**/*google_vignette*", route -> route.abort());
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * SUT Navigation
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	@Step("Navigate to the Test URL")
	private void navigateToAppBaseURL(String baseURL) {
		page.get().navigate(baseURL);
		logger.info("Navigated to Base URL: {}", baseURL);
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * GETTERS
	 * ══════════════════════════════════════════════════════════════════════════
	 */
	public Page getPage() {
		return page.get();
	}

	public BrowserContext getContext() {
		return context.get();
	}

	// ✅ Static getter — used by TestListener to get current thread's page
	// without needing a DriverManager instance
	public static Page getCurrentPage() {
		return page.get();
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * SETTERS
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	// ✅ Setter — called from BaseTest before initDriver()
	public static void setBsBrowser(String browser) {
		bsBrowserName.set(browser);
	}
}