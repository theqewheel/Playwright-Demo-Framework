package samples;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class BrowserSwitchingTabs {

	public static void main(String[] args) {

		Playwright playwright = Playwright.create();

		Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
				.setArgs(Arrays.asList("--start-mazimized")).setSlowMo(1000));

		// Opening a new tab by clicking on a link inside the parent window

		BrowserContext bx = browser.newContext();

		Page parentPage = bx.newPage();

		parentPage.navigate("https://opensource-demo.orangehrmlive.com");

		System.out.println("Parent: Page Title is - " + parentPage.title());

		Page childPage1 = parentPage.waitForPopup(() -> {

			parentPage.getByRole(AriaRole.LINK).first().click();

		});

		childPage1.waitForLoadState();

		System.out.println("ChildPage1: Page Title is - " + childPage1.title());

		// Opening a blank new tab from the parent window

		Page childPage2 = parentPage.waitForPopup(() -> {

			parentPage.click("a[target='_blank']"); // open up new blank tab

		});

		childPage2.waitForLoadState();

		childPage2.navigate("https://www.amazon.com");

		childPage2.waitForLoadState();

		System.out.println("ChildPage2: Page Title is - " + childPage2.title());

		Locator cntnuButton = childPage2.getByRole(AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Continue shopping"));

		if (cntnuButton.isVisible()) {
			cntnuButton.click();
			System.out
					.println("ChildPage2: Page Title after Navigation to Continue Shopping is - " + childPage2.title());
		}

		childPage2.getByText(Pattern.compile("You are on amazon.com")).isVisible();

		childPage2.getByText(Pattern.compile("Hello, sign in")).click();
		
		parentPage.bringToFront();
		
		parentPage.getByPlaceholder("Username").fill("Autobot_01");
		
		//childPage2.bringToFront();
		
		List<Page> allPages = bx.pages();
		
		for(Page p:allPages) {
			String title = p.title();
			if(title.contains("Amazon"))
			{
				p.bringToFront();
			}
		}

	}

}
