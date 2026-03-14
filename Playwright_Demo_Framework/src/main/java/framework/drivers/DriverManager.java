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

	private static final ThreadLocal<Playwright> playwright = new ThreadLocal<>();
	private static final ThreadLocal<Browser> browser = new ThreadLocal<>();
	private static final ThreadLocal<BrowserContext> context = new ThreadLocal<>();
	private static final ThreadLocal<Page> page = new ThreadLocal<>();
	private Logger logger = LogManager.getLogger(DriverManager.class);
	private boolean headlessMode;

	public void initDriver() {

		headlessMode = Boolean.parseBoolean(ConfigManager.getProperty("headless"));

		String baseURL = ConfigManager.getBaseURL();

		try {
			logger.info("Initiating Driver . . . . . ");

			playwright.set(Playwright.create());

			playwright.get().selectors().setTestIdAttribute(ConfigManager.getProperty("test-id"));

			browser.set(initBrowser());

			logger.info("Launching Browser. HeadlessMode: {}", String.valueOf(headlessMode));

			context.set(browser.get()
					.newContext(new Browser.NewContextOptions().setViewportSize(null).setAcceptDownloads(true)));

			removeAdds(context.get());

			logger.info("Browser Context Launched");

			page.set(context.get().newPage());

			logger.info("Browser Page opened.");

			page.get().navigate(baseURL);

			logger.info("Navigated to Base URL: {}", baseURL);

		} catch (Exception e) {

			logger.error("Exception during the driver initiation", e);

			throw new RuntimeException("Driver initialization failed", e);
		}

	}

	private Browser initBrowser() {

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

	public void quitDriver() {

		if (page.get() != null)
			page.get().close();
		if (context.get() != null)
			context.get().close();
		if (browser.get() != null)
			browser.get().close();
		if (playwright.get() != null)
			playwright.get().close();

		logger.info("Closed Browser !!");

		// to avoid memory leaks - critical
		page.remove();
		context.remove();
		browser.remove();
		playwright.remove();
	}

	public Page getPage() {
		return page.get();
	}

	public void removeAdds(BrowserContext context) {
		context.route("**/*doubleclick.net/**", route -> route.abort());
		context.route("**/*googlesyndication.com/**", route -> route.abort());
		context.route("**/*googleads.g.doubleclick.net/**", route -> route.abort());
		context.route("**/*adservice.google.com/**", route -> route.abort());
		context.route("**/*google_vignette*", route -> route.abort());
	}

}
