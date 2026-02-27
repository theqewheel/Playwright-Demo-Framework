package samples;

import java.util.Arrays;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Frame;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.AriaRole;

public class DialogDemo {

	public static void main(String[] args) {
		
		Playwright playwright = Playwright.create();
		
		Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false)
				.setArgs(Arrays.asList("--start-mazimized")).setSlowMo(3000));

		// Opening a new tab by clicking on a link inside the parent window

		BrowserContext bx = browser.newContext();

		Page parentPage = bx.newPage();
				
		parentPage.navigate("https://vinothqaacademy.com/iframe/");
		
		parentPage.onDialog(dialog -> {
			String msg = dialog.message();
			System.out.println("Alert says: " + msg);
			//Assert.assertTrue(msg.contains("alert box"));
			dialog.accept("test");
		});
		
		
		//switching to the frame
		Frame frame2 = parentPage.frame("popuppage");

		//first button
		//frame2.getByRole(AriaRole.BUTTON, new Frame.GetByRoleOptions().setName("Alert Box").setExact(true)).click();
		
		//second button
		//frame2.getByRole(AriaRole.BUTTON, new Frame.GetByRoleOptions().setName("Confirm Alert Box").setExact(true)).click();
		
		//third button
		frame2.getByRole(AriaRole.BUTTON, new Frame.GetByRoleOptions().setName("Prompt Alert Box").setExact(true)).click();
		
	}
}
