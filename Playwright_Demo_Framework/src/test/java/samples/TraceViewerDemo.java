package samples;

import java.nio.file.Paths;
import java.util.regex.Pattern;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.BrowserType.LaunchOptions;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.assertions.PlaywrightAssertions;

public class TraceViewerDemo {

	// constants
	static String app_Base_URL = "https://automationexercise.com/";
	static String username = "Autobot_" + System.currentTimeMillis();
	static String email = "Autobot_" + System.currentTimeMillis() + "@cloudyfolk.com";

	public static void main(String[] args) {

		Playwright playwright = Playwright.create();
		playwright.selectors().setTestIdAttribute("data-qa");

		LaunchOptions lp = new BrowserType.LaunchOptions().setHeadless(false).setSlowMo(1000);

		Browser browser = playwright.chromium().launch(lp);

		BrowserContext context = browser.newContext();

		// Starting Tracing
		context.tracing().start(new Tracing.StartOptions().setTitle("Automation Exercise Trace View")
				.setScreenshots(true).setSnapshots(true));

		Page page = context.newPage();

		// Test case - Step 1

		page.navigate(app_Base_URL);
		PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile("automationexercise"));
		PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile("Automation Exercise"));
		page.getByText(Pattern.compile("Signup", Pattern.CASE_INSENSITIVE)).click();
		PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile("login"));
		PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile("Signup", Pattern.CASE_INSENSITIVE));

		// Test case - Step 2
		page.getByPlaceholder("Name").fill(username);
		page.getByPlaceholder("Email Address").nth(1).fill(email);
		page.getByTestId("signup-button").click();
		PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile(".*/signup$"));
		PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile("Signup", Pattern.CASE_INSENSITIVE));
		PlaywrightAssertions.assertThat(page.getByText("Enter Account Information")).isVisible();

		// Stoping Tracing
		context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get("trace/trace.zip")));
		
		context.close();
		browser.close();
		playwright.close();
	}

}
