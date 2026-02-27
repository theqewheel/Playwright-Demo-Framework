package samples;

import java.util.Arrays;
import java.util.List;import java.util.regex.Pattern;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class IFrameDemo {
	
	public static void main(String[] args) {
		
		Playwright playwright = Playwright.create();
		
		Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
				.setArgs(Arrays.asList("--start-mazimized")).setSlowMo(1000));

		// Opening a new tab by clicking on a link inside the parent window

		BrowserContext bx = browser.newContext();

		Page parentPage = bx.newPage();
		
		
		parentPage.navigate("https://vinothqaacademy.com/iframe/");
		//parentPage.navigate("https://redbus.in");
		
		parentPage.waitForLoadState();
		
		List<Frame> allFrames = parentPage.frames();
		
		System.out.println("The total frames on the Page " + parentPage.title() + " is " + allFrames.size());
		
		/*
		 * parentPage.getByRole(AriaRole.BUTTON, new
		 * Page.GetByRoleOptions().setName("Account")).click();
		 * 
		 * parentPage.getByRole(AriaRole.BUTTON, new
		 * Page.GetByRoleOptions().setName("Sign up")).click();
		 * 
		 * parentPage.locator("//input[contains(@class,'Mobile')]").fill("9896812345");
		 * 
		 * //parentPage.locator("input[class*='Mobile']");
		 * 
		 * parentPage.frame("//iframe[@title='reCAPTCHA']").locator(
		 * "//span[@id='recaptcha-anchor']").click();
		 */
		
		Frame frame1 = parentPage.frame("employeetable");
		frame1.locator("#nameInput").fill("Autobot");
		frame1.locator("#roleInput").fill("Tester");
		frame1.locator("#addBtn").click();
		
		
		
		//https://vinothqaacademy.com/webtable/
		
	}

}
