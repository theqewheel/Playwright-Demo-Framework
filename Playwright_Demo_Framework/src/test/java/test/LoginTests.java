package test;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import pages.ae.HomePage;
import pages.ae.SignupDetailPage;
import pages.ae.SignupLoginPage;
import reporting.ReportManager;

public class LoginTests extends BaseTest {

	@Test(description = "AE2_TC02 - Verify that an existing user can login", groups = { "smoke", "regression" })
	@Epic("Login")
	@Feature("User Login")
	@Story("Login using Valid Credentials")
	@Severity(SeverityLevel.BLOCKER)
	@Owner("QE@Cloudyfolk")
	public void Test_AE2_TC02_Verify_Valid_User_Login() {
		
		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Login Page
		logger.info("Running:Step-2");
		ReportManager.logStep("Navigate to Login page");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Login");
		signupLoginPage.verifyPageHeader("login", "Login to your account");

		// Step-3 Enter Login Credentials and Login
		signupLoginPage.EnterLoginDetails("theqewheel@gmail.com", "admin@123");
		signupLoginPage.ClickLogin();
		signupLoginPage.verifyPageLoaded("automationexercise.com", "Automation Exercise");
		assertEquals(homePage.getLoggedInUsername(), "TheQEWheel", "Invalid username on login");

	}

	@Test(description = "AE2_TC03 - Verify that an invalid user cannot login", groups = { "regression" })
	@Epic("Login")
	@Feature("User Login")
	@Story("Login using Valid Credentials")
	@Severity(SeverityLevel.BLOCKER)
	@Owner("QE@Cloudyfolk")
	public void Test_AE2_TC03_Verify_InValid_User_Login() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Login Page
		logger.info("Running:Step-2");
		ReportManager.logStep("Navigate to Login page");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Login");
		signupLoginPage.verifyPageHeader("login", "Login to your account");

		// Step-3 Enter Login Credentials and Login
		signupLoginPage.EnterLoginDetails("autobot@gmail.com", "admin@123");
		signupLoginPage.ClickLogin();
		homePage.verifyInvalidLoginError();

	}

	@Test(description = "AE2_TC05 - Verify logging out from the application", groups = { "regression" })
	@Epic("Login")
	@Feature("User Login")
	@Story("Login using Valid Credentials")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE2_TC04_Verify_User_Logout() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Login Page
		logger.info("Running:Step-2");
		ReportManager.logStep("Navigate to Login page");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Login");
		signupLoginPage.verifyPageHeader("login", "Login to your account");

		// Step-3 Enter Login Credentials and Login
		signupLoginPage.EnterLoginDetails("theqewheel@gmail.com", "admin@123");
		signupLoginPage.ClickLogin();
		homePage.verifyPageLoaded("automationexercise.com", "Automation Exercise");
		
		// Step-4 Logout
		homePage.LogOut();
		homePage.verifyPageLoaded("/login", "Login");

	}
}
