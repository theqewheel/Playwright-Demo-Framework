package samples;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class Test_Sample_Test1 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		try (Playwright playwright = Playwright.create()) {

			// Launching the browser and navigating to the URL

			Browser browser = playwright.chromium().launch(
					new BrowserType.LaunchOptions().setHeadless(false).setChannel("chrome").setSlowMo(1000));
			Page page = browser.newPage();
			page.navigate("https://google.com");

			// Verify the page title
			String expectedTitle = "Google - Google";
			String actualTitle = page.title();
			
			if (actualTitle.equals(expectedTitle)) {
				System.out.println("The page title is correct: " + actualTitle);
			} else {
				System.out.println("The page title is incorrect. "
						+ "Expected: " + expectedTitle + ", but got: " + actualTitle);
			}
			
			browser.close();

		}

		catch (Exception e) {
			System.out.println("The exception is: " + e.getMessage());
		}

	}
}