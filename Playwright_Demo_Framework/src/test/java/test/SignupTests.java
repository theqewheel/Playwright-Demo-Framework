package test;

import java.util.UUID;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.assertions.PlaywrightAssertions;
import com.microsoft.playwright.options.AriaRole;

import base.BaseTest;
import framework.pages.ae.HomePage;

public class SignupTests extends BaseTest{
	
	//constants
	static String app_Base_URL = "https://automationexercise.com/";
	
	
	@Test(description = "AE001_TC01 - Verify that a new user can sign up successfully")
	public void Test_AE001_TC01_Verify_New_User_SignUp() {
		
		HomePage homePage = new HomePage(page);
	
		//Step-1 Navigate to the URL
		System.out.println("Running:Step-1");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");
		
	}
	
	

}
