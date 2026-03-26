package framework.config;

import java.io.InputStream;
import java.util.Properties;

import org.testng.ISuite;

import framework.logging.LogManager;
import framework.logging.UniversalLogger;

public class ConfigManager {

	private static Properties properties = new Properties();
	protected static final UniversalLogger logger = LogManager.getLogger(ConfigManager.class);
	private static String environment;

	static {
		try {
			InputStream input = ConfigManager.class.getClassLoader().getResourceAsStream("config.properties");

			if (input == null) {
				logger.error("Runtime Exception: ",
						new RuntimeException("config.properties file not found in classpath"));
			}

			properties.load(input);
			logAllProperties();
		} catch (Exception e) {
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

		// System property - first priority (CLI or CI)
		String browser = System.getProperty("browser");

		if (browser == null || browser.isEmpty()) {
			browser = properties.getProperty("browser", "chromium"); // Default to chromium if not specified
		}

		return browser.toLowerCase();
	}

	public static void logAllProperties() {
		properties.forEach((key, value) -> {
			logger.debug(key + " = " + value);
		});

	}

	// Called once for the Listener
	public static void initializeEnvironment(ISuite suite) {

		// System property - first priority (CLI or CI)
		String env = System.getProperty("environment");

		// TestNG XML parameter - second priority
		if (env == null || env.isEmpty()) {
			env = suite.getParameter("environment");
		}

		// Config file property - third priority
		if (env == null || env.isEmpty()) {
			env = getProperty("env");
		}

		// Default to 'qa' if no environment is specified
		if (env == null || env.isEmpty()) {
			env = "qa";
			logger.warn("No environment specified. Defaulting to 'qa'");
		}

		environment = env;

		// Optional for using unified environment variable in logs
		System.setProperty("environment", env);

	}

	// Called from Test Listener
	public static String getEnvironment() {

		if (environment == null || environment.isEmpty()) {
			logger.warn("Environment not initialized. Defaulting to 'qa'");
			environment = "qa";
		}

		return environment.toLowerCase();
	}

	// Called from Test Driver
	public static boolean getHeadlessmode() {

		switch (getExecutionMode().toLowerCase()) {
		case "github":
		case "native":
			return true;
		case "browserstack":
		case "local":
		default:
			return Boolean.parseBoolean(getProperty("headless"));

		}

	}

	// ══════════════════════════════════════════════════════════════════════════
	// EXECUTION MODE
	// Priority: System property → config.properties → default "local"
	// ══════════════════════════════════════════════════════════════════════════

	public static String getExecutionMode() {

		// System property - set by GitHub Actions via -Dexecution.mode
		String mode = System.getProperty("execution.mode");

		// Fallback to mode in config.properties
		if (mode == null || mode.isEmpty()) {
			mode = getProperty("execution.mode");
			logger.info("Execution mode falls back to Config.properties");
		}

		// Fallback to local - default
		if (mode == null || mode.isEmpty()) {
			mode = "local";
			logger.info("Execution mode falls back to default - 'Local'");
		}

		return mode.toLowerCase().trim();
	}

	// ══════════════════════════════════════════════════════════════════════════
	// BROWSERSTACK CREDENTIALS
	// Passed via -Dbs.username and -Dbs.accesskey from GitHub Actions secrets
	// Never hardcoded — always from system properties
	// ══════════════════════════════════════════════════════════════════════════

	public static String getBSUsername() {
		String val = System.getProperty("bs.username");
		// if(val==null || val.isEmpty()) val = getProperty("bs.username");
		return val;
	}

	public static String getBSAccessKey() {
		String val = System.getProperty("bs.accesskey");
		// if(val==null || val.isEmpty()) val = getProperty("bs.accesskey");
		return val;
	}

	// ══════════════════════════════════════════════════════════════════════════
	// BROWSERSTACK CAPABILITIES
	// ══════════════════════════════════════════════════════════════════════════

	public static String getBSProjectName() {
		return getProperty("bs.project.name");
	}

	public static String getBSBuildName() {
		String baseName = getProperty("bs.build.name");

		// ✅ Append GitHub run number if available (CI runs)
		// Falls back to timestamp for local BS runs
		String runNumber = System.getenv("GITHUB_RUN_NUMBER");

		if (runNumber != null && !runNumber.isEmpty()) {
			return baseName + " #" + runNumber;
			// e.g. "Release 1.0 - AE Tests #47"
		}

		// For local BrowserStack runs — append timestamp
		return baseName + " [local-" + new java.text.SimpleDateFormat("MMdd-HHmm").format(new java.util.Date()) + "]";
		// e.g. "Release 1.0 - AE Tests [local-0326-1430]"
	}

}
