package framework.drivers;

import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.MDC;

import com.google.gson.Gson;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;

import framework.config.ConfigManager;
import framework.logging.LogManager;
import framework.logging.UniversalLogger;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.databind.JsonNode;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;

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
	private static final ThreadLocal<String> bsSessionId = new ThreadLocal<>();
	private static final ThreadLocal<Path> testLogFilePath = new ThreadLocal<>();
	private static final ThreadLocal<String> bsTestName = new ThreadLocal<>();

	private UniversalLogger logger = LogManager.getLogger(DriverManager.class);
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

			// ── Connect via Playwright CDP ─────────────────────────────────────
			playwright.set(Playwright.create());
			playwright.get().selectors().setTestIdAttribute(ConfigManager.getProperty("test-id"));

			browser.set(initBrowserStackBrowser());

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

			extractBSSessionId();

			navigateToAppBaseURL(baseURL);

		} catch (Exception e) {
			logger.error("Exception during BROWSERSTACK driver initiation", e);
			throw new RuntimeException("BROWSERSTACK Driver initialization failed", e);
		}
	}

	/**
	 * ══════════════════════════════════════════════════════════════════════════
	 * BROWSERSTACK BROWSER SELECTION
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	private Browser initBrowserStackBrowser() {

		String username = ConfigManager.getBSUsername();
		String accessKey = ConfigManager.getBSAccessKey();

		// Step 1 — determine BS browser name and Playwright type first
		String bsBrowser = bsBrowserName.get();
		if (bsBrowser == null || bsBrowser.isEmpty())
			bsBrowser = "chrome";

		// Step 2 — build caps with correct BS browser name
		Map<String, Object> caps = new HashMap<>();
		caps.put("browser_version", "latest");
		caps.put("os", "Windows");
		caps.put("os_version", "11");
		caps.put("name", getBrowserStackTestName());
		caps.put("build", ConfigManager.getBSBuildName());
		caps.put("project", ConfigManager.getBSProjectName());
		caps.put("sessionName", getBrowserStackTestName());
		caps.put("browserstack.username", username);
		caps.put("browserstack.accessKey", accessKey);
		caps.put("browserstack.debug", "true");
		caps.put("browserstack.networkLogs", "true"); // enables Network Logs tab on browser stack
		caps.put("browserstack.console", "verbose"); // enables Console tab on browser stack
		caps.put("browserstack.playwrightLogs", "true"); // enables Playwright tab on browser stack

		// Step 3 — set correct browser in caps + build endpoint
		switch (bsBrowser.toLowerCase()) {
		case "firefox":
			caps.put("browser", "playwright-firefox");
			break;
		case "webkit":
			caps.put("browser", "playwright-webkit");
			break;
		case "edge":
			caps.put("browser", "edge");
			break;
		default:
			caps.put("browser", "chrome");
			break;
		}

		logger.info("Connecting to BrowserStack. Browser: {}", bsBrowser);

		// Step 4 — encode caps and build WS URL
		String capsJson = new Gson().toJson(caps);
		String capsEncoded = URLEncoder.encode(capsJson, StandardCharsets.UTF_8);
		String wsEndpoint = "wss://cdp.browserstack.com/playwright?caps=" + capsEncoded;

		// Step 5 — connect with correct Playwright browser type
		switch (bsBrowser.toLowerCase()) {
		case "firefox":
			return playwright.get().firefox().connect(wsEndpoint);
		case "webkit":
			return playwright.get().webkit().connect(wsEndpoint);
		default: // chrome, edge, chromium
			return playwright.get().chromium().connect(wsEndpoint);
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
			bsTestName.remove(); 
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
	 * HELPER METHODS
	 * ══════════════════════════════════════════════════════════════════════════
	 */

	// get the testname for the browser stack display
	private String getBrowserStackTestName() {
		// ✅ Primary — set directly from BaseTest.setup() before initDriver()
		String name = bsTestName.get();
		if (name != null && !name.isEmpty())
			return name;

		// Fallback — MDC (may not be set yet at cap-build time)
		name = MDC.get("testname");
		if (name != null && !name.isEmpty())
			return name;

		return "Unknown_Test";
	}

	// upload logs to BS
	public void uploadTerminalLogsToBrowserStack(String testName) {
		try {
			String sessionId = bsSessionId.get();
			if (sessionId == null || sessionId.isEmpty()) {
				logger.warn("No BS session ID — skipping terminal log upload");
				return;
			}

			// ✅ Flush Logback before reading — ensures all lines are written to disk
			flushTestLogFile();

			// Small buffer after flush — gives OS time to complete file write
			Thread.sleep(500);

			Path logFile = Path.of("logs/tests/" + testName + ".log");
			if (!Files.exists(logFile)) {
				logger.warn("Log file not found for BS upload: {}", testName);
				return;
			}

			// ✅ Size check — BrowserStack has upload size limits
			long fileSizeKB = Files.size(logFile) / 1024;
			logger.info("Log file size: {} KB", fileSizeKB);

			if (fileSizeKB > 5120) {
				logger.warn("Log file too large for BS upload: {} KB — skipping", fileSizeKB);
				return;
			}

			String username = ConfigManager.getBSUsername();
			String accessKey = ConfigManager.getBSAccessKey();
			String credentials = Base64.getEncoder().encodeToString((username + ":" + accessKey).getBytes());

			String boundary = "----Boundary" + System.currentTimeMillis();
			byte[] fileBytes = Files.readAllBytes(logFile);

			String header = "--" + boundary + "\r\n" + "Content-Disposition: form-data; name=\"file\"; " + "filename=\""
					+ testName + ".log\"\r\n" + "Content-Type: text/plain\r\n\r\n";
			String footer = "\r\n--" + boundary + "--\r\n";

			byte[] body = concatBytes(header.getBytes(), fileBytes, footer.getBytes());

			HttpClient client = HttpClient.newHttpClient();
			HttpRequest request = HttpRequest.newBuilder()
					.uri(URI.create(
							"https://api-cloud.browserstack.com/automate/sessions/" + sessionId + "/terminallogs"))
					.header("Authorization", "Basic " + credentials)
					.header("Content-Type", "multipart/form-data; boundary=" + boundary)
					.POST(HttpRequest.BodyPublishers.ofByteArray(body)).build();

			HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

			logger.info("BS terminal log upload: {} — {}", response.statusCode(), response.body());

			if (response.statusCode() == 200) {
				logger.info("✅ Terminal logs uploaded to BrowserStack successfully.");
			} else if (response.statusCode() == 404) {
				logger.warn("⚠️ BS session not found — session may have expired. "
						+ "Consider uploading before quitDriver().");
			} else if (response.statusCode() == 401) {
				logger.error("❌ BS credentials invalid — check bs.username and bs.accesskey.");
			} else {
				logger.warn("⚠️ Unexpected BS response: {} — {}", response.statusCode(), response.body());
			}

		} catch (Exception e) {
			logger.warn("Could not upload terminal logs to BS: {}", e.getMessage());
		} finally {
			bsSessionId.remove(); // ✅ always clean up session ID
		}
	}

	private byte[] concatBytes(byte[]... arrays) {
		int total = 0;
		for (byte[] a : arrays)
			total += a.length;
		byte[] result = new byte[total];
		int pos = 0;
		for (byte[] a : arrays) {
			System.arraycopy(a, 0, result, pos, a.length);
			pos += a.length;
		}
		return result;
	}

	private void extractBSSessionId() {
	    try {
	        String username  = ConfigManager.getBSUsername();
	        String accessKey = ConfigManager.getBSAccessKey();
	        String buildName = ConfigManager.getBSBuildName();
	        
	        // ✅ This thread's browser — used to match the correct session
	        String thisBrowser = bsBrowserName.get();
	        if (thisBrowser == null) thisBrowser = "chrome";

	        String credentials = Base64.getEncoder()
	            .encodeToString((username + ":" + accessKey).getBytes());

	        HttpClient client = HttpClient.newHttpClient();

	        // Step 1 — get build ID
	        HttpRequest buildRequest = HttpRequest.newBuilder()
	            .uri(URI.create(
	                "https://api.browserstack.com/automate/builds.json"))
	            .header("Authorization", "Basic " + credentials)
	            .GET().build();

	        HttpResponse<String> buildResponse = client.send(
	            buildRequest, HttpResponse.BodyHandlers.ofString());

	        if (buildResponse.statusCode() != 200) {
	            logger.warn("Could not fetch BS builds: {}",
	                buildResponse.statusCode());
	            return;
	        }

	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode builds = mapper.readTree(buildResponse.body());
	        String buildId = null;

	        // Match build by name first, fallback to most recent
	        for (JsonNode build : builds) {
	            JsonNode automation = build.get("automation_build");
	            if (automation != null && buildName.equals(
	                    automation.get("name").asText())) {
	                buildId = automation.get("hashed_id").asText();
	                break;
	            }
	        }

	        if (buildId == null && builds.size() > 0) {
	            buildId = builds.get(0)
	                           .get("automation_build")
	                           .get("hashed_id").asText();
	        }

	        if (buildId == null) {
	            logger.warn("No BS build found — cannot extract session ID");
	            return;
	        }

	        // Step 2 — get ALL running sessions for this build
	        // ✅ No limit=1 — fetch all running and match by browser
	        HttpRequest sessionRequest = HttpRequest.newBuilder()
	            .uri(URI.create(
	                "https://api.browserstack.com/automate/builds/"
	                + buildId + "/sessions.json?status=running"))
	            .header("Authorization", "Basic " + credentials)
	            .GET().build();

	        HttpResponse<String> sessionResponse = client.send(
	            sessionRequest, HttpResponse.BodyHandlers.ofString());

	        if (sessionResponse.statusCode() != 200) {
	            logger.warn("Could not fetch BS sessions: {}",
	                sessionResponse.statusCode());
	            return;
	        }

	        JsonNode sessions = mapper.readTree(sessionResponse.body());

	        // ✅ Match session by browser name — each thread finds its OWN session
	        for (JsonNode sessionNode : sessions) {
	            JsonNode session = sessionNode.get("automation_session");
	            if (session == null) continue;

	            String sessionBrowser = session.has("browser") 
	                ? session.get("browser").asText("").toLowerCase() 
	                : "";

	            // ✅ Normalize browser names for comparison
	            // BS returns "chrome", "firefox", "edge" 
	            // your caps use "chrome", "playwright-firefox", "edge"
	            String normalizedCap = thisBrowser.toLowerCase()
	                .replace("playwright-", ""); // strip playwright- prefix

	            if (sessionBrowser.contains(normalizedCap) 
	                    || normalizedCap.contains(sessionBrowser)) {
	                String sessionId = session.get("hashed_id").asText();
	                bsSessionId.set(sessionId);
	                logger.info("✅ BS Session ID matched for browser {}: {}",
	                    thisBrowser, sessionId);
	                return;
	            }
	        }

	        // ✅ Fallback — if browser match fails, take most recent unmatched session
	        // This handles edge cases like webkit or unknown browser names
	        if (sessions.size() > 0) {
	            String sessionId = sessions.get(0)
	                .get("automation_session")
	                .get("hashed_id").asText();
	            bsSessionId.set(sessionId);
	            logger.warn("⚠️ Browser match failed for {} — using most recent session: {}",
	                thisBrowser, sessionId);
	        }

	    } catch (Exception e) {
	        logger.warn("BS session ID extraction failed: {}", e.getMessage());
	    }
	}

	// Forces Logback SiftingAppender to flush the current test's log file
	// Must be called BEFORE reading the file for upload
	private void flushTestLogFile() {
		try {
			ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) org.slf4j.LoggerFactory
					.getILoggerFactory();

			for (ch.qos.logback.classic.Logger logbackLogger : loggerContext.getLoggerList()) {
				for (java.util.Iterator<ch.qos.logback.core.Appender<ch.qos.logback.classic.spi.ILoggingEvent>> it = logbackLogger
						.iteratorForAppenders(); it.hasNext();) {

					ch.qos.logback.core.Appender<ch.qos.logback.classic.spi.ILoggingEvent> appender = it.next();

					if (appender instanceof ch.qos.logback.core.FileAppender) {
						((ch.qos.logback.core.FileAppender<?>) appender).stop();
						((ch.qos.logback.core.FileAppender<?>) appender).start();
					}
				}
			}
			logger.debug("Logback file appenders flushed.");
		} catch (Exception e) {
			logger.warn("Could not flush Logback appenders: {}", e.getMessage());
		}
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

	// Getter for bs session id

	public static String getBsSessionId() {
		return bsSessionId.get();
	}

	// Getter for log path
	public Path getTestLogFilePath() {
		return testLogFilePath.get();
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

	public static void setBsTestName(String testName) {
		bsTestName.set(testName);
	}
}