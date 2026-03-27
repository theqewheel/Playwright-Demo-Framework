package framework.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.microsoft.playwright.Page;

import framework.config.ConfigManager;
import framework.drivers.DriverManager;

public class UniversalLogger {
	
	private final Logger logger;
    private final boolean isBrowserStack;

    public UniversalLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
        this.isBrowserStack = "browserstack".equalsIgnoreCase(
            ConfigManager.getExecutionMode()
        );
    }

    // ══════════════════════════════════════════════════════
    // INFO
    // ══════════════════════════════════════════════════════
    public void info(String message) {
        logger.info(message);
        annotate(message, "info");
    }

    public void info(String message, Object... args) {
        logger.info(message, args);
        annotate(formatMessage(message, args), "info");
    }

    // ══════════════════════════════════════════════════════
    // ERROR
    // ══════════════════════════════════════════════════════
    public void error(String message) {
        logger.error(message);
        annotate(message, "error");
    }

    public void error(String message, Object... args) {
        logger.error(message, args);
        annotate(formatMessage(message, args), "error");
    }

    public void error(String message, Throwable t) {
        logger.error(message, t);
        annotate(message + " — " + t.getMessage(), "error");
    }

    // ══════════════════════════════════════════════════════
    // WARN
    // ══════════════════════════════════════════════════════
    public void warn(String message) {
        logger.warn(message);
        annotate(message, "warning");
    }

    public void warn(String message, Object... args) {
        logger.warn(message, args);
        annotate(formatMessage(message, args), "warning");
    }

    // ══════════════════════════════════════════════════════
    // DEBUG
    // ══════════════════════════════════════════════════════
    public void debug(String message) {
        logger.debug(message);
        annotate(message, "debug");
    }

    public void debug(String message, Object... args) {
        logger.debug(message, args);
        annotate(formatMessage(message, args), "debug");
    }

    // ══════════════════════════════════════════════════════
    // PRIVATE — BrowserStack annotate
    // ══════════════════════════════════════════════════════
    private void annotate(String message, String level) {
        if (!isBrowserStack) return;

        try {
            Page page = DriverManager.getCurrentPage();
            if (page == null) return;

            // Escape quotes to avoid breaking the JSON payload
            String safeMessage = message
                .replace("\\", "\\\\")
                .replace("\"", "'");

            String script = "browserstack_executor: {" +
                "\"action\": \"annotate\", " +
                "\"arguments\": {" +
                    "\"data\": \"[" + level.toUpperCase() + "] " + safeMessage + "\", " +
                    "\"level\": \"" + level + "\"" +
                "}" +
            "}";

            page.evaluate("_ => {}", script);

        } catch (Exception e) {
            // ✅ Silent fail — never let BS annotation break your test
            logger.warn("Could not annotate BrowserStack session: {}", e.getMessage());
        }
    }

    // ══════════════════════════════════════════════════════
    // PRIVATE — SLF4J style {} placeholder formatting
    // ══════════════════════════════════════════════════════
    private String formatMessage(String message, Object... args) {
        if (args == null || args.length == 0) return message;
        String result = message;
        for (Object arg : args) {
            int idx = result.indexOf("{}");
            if (idx == -1) break;
            String argStr = (arg != null) ? arg.toString() : "null";
            result = result.substring(0, idx) + argStr + result.substring(idx + 2);
        }
        return result;
    }

}
