package samples;

import java.util.Arrays;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class BrowserContextDemo {

	public static void main(String[] args) {
		
		Playwright playwright = Playwright.create();
		Browser browser = playwright.chromium()
				.launch(new BrowserType.LaunchOptions()
						.setHeadless(false)
						.setArgs(Arrays.asList("--start-mazimized"))
						.setSlowMo(1000));
		
		
		//Demo-1 Opening a Bowser Page
		Page page = browser.newPage();
		
		page.navigate("https://www.google.com");
		
		System.out.println("Demo1: Page Title is - " + page.title());
		
		page.close();
		
		//Demo-2 Opening two Browser Context
		BrowserContext bx1 = browser.newContext();
		BrowserContext bx2 = browser.newContext();
		
		Page page1 = bx1.newPage();
		Page page2 = bx2.newPage();
		
		page1.navigate("https://www.google.com");
		page2.navigate("https://www.amazon.com");
		
		System.out.println("Demo2: Page Title is - " + page1.title());
		System.out.println("Demo2: Page Title is - " + page2.title());
		
		page1.close();
		page2.close();
		bx1.close();
		bx2.close();
		
		//Demo-3 Opening a new tab by clicking on a link inside the parent window
		
		BrowserContext bx3 = browser.newContext();
		
		Page page3 = bx3.newPage();
		
		page3.navigate("https://opensource-demo.orangehrmlive.com");
		
		System.out.println("Demo3: Page Title is - " + page3.title());
		
		Page popup = page3.waitForPopup(() -> {
			
			page3.getByRole(AriaRole.LINK).first().click();
			
		});
		
		System.out.println("Demo3: Pop-up Page Title is - " + popup.title());
		
		popup.close();
		
		System.out.println("Demo3: Parent Page Title is - " + page3.title());
		
		page3.close();
		
		bx3.close();
		
		//Demo-4 Opening a blank new tab from the parent window
		
		BrowserContext bx4 = browser.newContext();
				
		Page page4 = bx4.newPage();
				
		page4.navigate("https://opensource-demo.orangehrmlive.com");
				
		System.out.println("Demo4: Page Title is - " + page4.title());
				
		Page popup2 = page4.waitForPopup(() -> {
					
			page4.click("a[target='_blank']"); //open up new blank tab
					
		});
		
		popup2.waitForLoadState();
		
		popup2.navigate("https://www.amazon.com");
				
		System.out.println("Demo4: Pop-up Page Title is - " + popup2.title());
				
		popup2.close();
				
		System.out.println("Demo4: Parent Page Title is - " + page4.title());
				
		page4.close();
						

	}

}
