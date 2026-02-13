package base;

import java.util.Arrays;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class BaseTest {
	protected Playwright playwright;
	protected Browser browser;
	protected Page page;
	protected String baseURL = "https://automationexercise.com/";
	

	@BeforeMethod
	public void setup() {
		playwright = Playwright.create();
		browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
				// .setChannel("chrome")
				.setSlowMo(1000).setArgs(Arrays.asList("--start-maximized")));
		page = browser.newPage();
		System.out.println("Browser launched successfully");
		page.navigate(baseURL);
		System.out.println("Navigated to base URL: " + baseURL);

	}

	@AfterMethod
	public void teardown() {
		if (browser != null)
			browser.close();
		if (playwright != null)
			playwright.close();
		System.out.println("This is the teardown method");
	}

}
