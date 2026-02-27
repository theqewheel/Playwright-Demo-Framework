package samples;

import org.testng.annotations.Test;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import base.BaseTest;

public class Test_Sample_Google_Validations2 extends BaseTest {
	
	@Test
	public void verifyPageTitle2() {
		
		String expectedTitle = "Google";
		String actualTitle = page.title();
		
		if (actualTitle.equals(expectedTitle)) {
			System.out.println("The page title is correct: " + actualTitle);
		} else {
			System.out.println("The page title is incorrect. Expected: " + expectedTitle + ", but got: " + actualTitle);
		}
	}
	
	@Test
	public void verifySearchBox2() {
		
		Locator GoogleSearchBtn = page.getByRole(AriaRole.BUTTON,new Page.GetByRoleOptions().setName("Google Search"));
		
		String expectedText = "Google Search";
		String actualText = GoogleSearchBtn.textContent();
		
		assert actualText==expectedText : "The button text is incorrect. Expected: " + expectedText + ", but got: " + actualText;
	}
}
