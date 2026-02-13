package test;

import java.util.UUID;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;

import base.BaseTest;

public class Test_AE001_Verify_New_User_SignUp extends BaseTest{
	
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
		System.out.println("Running:Step-3");
		String username = "Autobot_" + System.currentTimeMillis();
		String email = "Autobot_" + System.currentTimeMillis() + "@cloudyfolk.com";
		String password = "@Pwd"+ UUID.randomUUID().toString().substring(0, 5);
		playwright.selectors().setTestIdAttribute("data-qa");
		page.getByPlaceholder("Name").fill(username);
		//page.getByPlaceholder("Email Address").fill(email);
		page.getByPlaceholder("Email Address").nth(1).fill(email);
		//page.getByTestId("signup-email").click();
		page.getByTestId("signup-button").click();
		PlaywrightAssertions.assertThat(page).hasURL(Pattern.compile(".*/signup$"));
		PlaywrightAssertions.assertThat(page).hasTitle(Pattern.compile("Signup",Pattern.CASE_INSENSITIVE));
		PlaywrightAssertions.assertThat(page.getByText("Enter Account Information")).isVisible();
		
		//Step-4 Enter the Account information
		System.out.println("Running:Step-4");
		Assert.assertTrue(page.locator("#name").getAttribute("value").equals(username));
		Assert.assertTrue(page.locator("#email").getAttribute("value").equals(email));
		//page.getByLabel(Pattern.compile("^\\s*Mr.\\s*$")).click();
		page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(Pattern.compile("^\\s*Mr.\\s*$"))).click();
		page.getByTestId("password").fill(password);
		page.getByTestId("days").selectOption("10");
		page.getByTestId("months").selectOption("May");
		page.getByTestId("years").selectOption("1990");
		page.getByText("Sign up for our newsletter!").click();
		page.getByText("Receive special offers from our partners!").click();
		PlaywrightAssertions.assertThat(page.locator("#newsletter")).isChecked();
		PlaywrightAssertions.assertThat(page.locator("#optin")).isChecked();
		
		//Step-5 Enter the Address information
		System.out.println("Running:Step-5");
		page.getByTestId("first_name").fill(username);
		page.getByTestId("last_name").fill("Bot"+System.currentTimeMillis());
		page.getByTestId("company").fill("CloudyFolk");
		page.getByTestId("address").fill("House No#33, Indira Nagar");
		page.getByTestId("address2").fill("Near Velammal School");
		page.getByTestId("country").selectOption("India");
		page.getByTestId("state").fill("Tamil Nadu");
		page.getByTestId("city").fill("Pollachi");
		page.getByTestId("zipcode").fill("600123");
		page.getByTestId("mobile_number").fill("90123456789");
		
		//Step-6 Click on the 'Create Account' button
		System.out.println("Running:Step-6");
		page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Create Account",Pattern.CASE_INSENSITIVE))).click();
		PlaywrightAssertions.assertThat(page.getByText(Pattern.compile("Account Created"))).isVisible();
		PlaywrightAssertions.assertThat(page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Account Created")))).isVisible();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Continue",Pattern.CASE_INSENSITIVE))).click();
		
		//Step-7 Verify that the user is logged in and the account name is displayed on the top right corner
		System.out.println("Running:Step-7");
		PlaywrightAssertions.assertThat(page.getByText("Logged in as " + username)).isVisible();
		
		//Step-8 Click on the 'Delete Account' button and verify that the account is deleted successfully
		System.out.println("Running:Step-8");
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Delete Account",Pattern.CASE_INSENSITIVE))).click();
		PlaywrightAssertions.assertThat(page.getByText(Pattern.compile("Account Deleted"))).isVisible();
		PlaywrightAssertions.assertThat(page.getByText(Pattern.compile("permanently deleted",Pattern.CASE_INSENSITIVE))).isVisible();
		page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Continue",Pattern.CASE_INSENSITIVE))).click();
	}
	
	

}
