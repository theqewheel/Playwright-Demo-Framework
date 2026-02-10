package test;

import java.util.regex.Pattern;

import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;

import base.Basetest;

public class Test_AE001_Verify_New_User_SignUp extends Basetest{
	
	//constants
	static String app_Base_URL = "https://automationexercise.com/";
	
	
	@Test(description = "AE001_TC01 - Verify that a new user can sign up successfully")
	public void Test_AE001_TC01_Verify_New_User_SignUp() {
		
		//Step-1 Navigate to the URL
		System.out.println("Running:Step-1");
		page.navigate(app_Base_URL);
		//PlaywrightAssertions.assertThat(page).hasURL(app_Base_URL);
		PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile("automationexercise"));
		PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile("Automation Exercise"));
		
		//Step-2 Click on the 'Signup' link	
		System.out.println("Running:Step-2");
		//page.locator("//a[contains(text(),'Signup')]").click();
		page.getByText(Pattern.compile("Signup",Pattern.CASE_INSENSITIVE)).click();
		PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile("login"));
		PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile("Signup",Pattern.CASE_INSENSITIVE));
		String expectedheader = "New User Signup!";
		//String actualheader = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Signup"))).textContent();
		//Assert.assertTrue(actualheader.contains(expectedheader), "Expected Page header - " + expectedheader + " is not displayed. Actual header is: " + actualheader);
		PlaywrightAssertions.assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Signup")))).containsText(expectedheader);
		//PlaywrightAssertions.assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Signup")))).hasText(expectedheader);
	
		//Step-3 Enter sign-up details and submit the form
		page.locator("//input[@placeholder='Name']").fill("Test User");
		page.locator("//input[@placeholder='Name']").fill("Test User");
		page.locator("//input[@placeholder='Name']").click();
	
	}
	
	

}
