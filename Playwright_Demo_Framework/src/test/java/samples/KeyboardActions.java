package samples;

import java.util.Arrays;
import java.util.List;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;

public class KeyboardActions {

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

		Frame frame1 = parentPage.frame("employeetable");
		
		frame1.locator("#nameInput").fill("Autobot");
		
		//copy from
		parentPage.keyboard().press("Control+A");
		parentPage.keyboard().press("Control+C");
		
		//move
		parentPage.keyboard().press("Tab");
		
		//frame1.locator("#roleInput").click();
		parentPage.keyboard().press("Control+V");
		
		frame1.locator("#addBtn").click();
		

	}

}
