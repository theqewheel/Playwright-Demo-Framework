package test.ae;

import java.util.UUID;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reporting.ReportManager;

public class SignupTests extends BaseTest {
	
	String username = "Autobot_" + System.currentTimeMillis();
	String email = "Autobot_" + System.currentTimeMillis() + "@cloudyfolk.com";
	String password = "@Pwd" + UUID.randomUUID().toString().substring(0, 5);

	@Test(description = "AE1_TC01 - Verify that a new user can sign up successfully", groups = { "smoke",
			"regression" })
	@Epic("Signup")
	@Feature("User Registration")
	@Story("Signup using Valid Credentials")
	@Severity(SeverityLevel.BLOCKER)
	@Owner("QE@Cloudyfolk")
	public void Test_AE1_TC01_Verify_New_User_SignUp() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Sign-up Page
		logger.info("Running:Step-2");
		ReportManager.logStep("Navigate to Sign-Up page");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");
		signupLoginPage.verifyPageHeader("signup", "New User Signup!");

		// Step-3 Enter sign-up details and submit the form
		logger.info("Running:Step-3");
		ReportManager.logStep("On Successfull Navigation, entering the signup credentials");
		signupLoginPage.EnterSignupDetails(username, email);
		signupLoginPage.ClickSignup();
		ReportManager.logStep("On Successfull Signup, Verify if the Detail SignUp Page is loaded");
		signupDetailPage.verifyPageLoaded("/signup", "Signup");
		signupDetailPage.verifyTextMessageDisplayed("Account Information", false);
		signupDetailPage.verifyTextMessageDisplayed("Address Information", true);
		ReportManager.logStep("Verify Auto-populated Username and Email");
		signupDetailPage.verifyAutoPopulatedNameAndEmail(username, email);

		// Step-4 Enter the Account information
		logger.info("Running:Step-4");
		ReportManager.logStep("Entering Account Information");
		signupDetailPage.selectGender("male");
		signupDetailPage.enterPassword(password);
		signupDetailPage.selectDateOfBirth("10", "May", "1990");
		signupDetailPage.optNewsLetter(true);
		signupDetailPage.optSpecialOffers(false);

		// Step-5 Enter the Address information
		logger.info("Running:Step-5");
		ReportManager.logStep("Enter Address Information");
		signupDetailPage.enterFirstAndLastName(username, "bot");
		signupDetailPage.enterCompany("CloudyFolk");
		signupDetailPage.enterAddress("House No#33, Indira Nagar");
		signupDetailPage.enterAddress2("Near Velammal School");
		signupDetailPage.selectCountry("India");
		signupDetailPage.enterState("Tamil Nadu");
		signupDetailPage.enterCity("Pollachi");
		signupDetailPage.enterZipcode("600123");
		signupDetailPage.enterMobileNumber("90123456789");
		signupDetailPage.optSpecialOffers(false);
		captureScreenshot("Entering Address");

		// Step-6 Create Account and verify that account is created successfully
		logger.info("Running:Step-6");
		ReportManager.logStep("Verify Account Creation is Successful");
		signupDetailPage.clickCreateAccount();
		signupDetailPage.verifyTextMessageDisplayed("Account Created", false);
		signupDetailPage.clickContinue();
		ReportManager.logStep("Verify Auto-Login is Successful for the newly registered user");
		homePage.verifyTextMessageDisplayed("Logged in as " + username, true);

		// Step-7 Optional Step: Clean up the test account created
		ReportManager.logStep("Verify Account Deletion is Successful");
		homePage.clickDeleteAccount();
		homePage.verifyTextMessageDisplayed("Account Deleted", false);
		homePage.verifyTextMessageDisplayed("permanently deleted", false);
		homePage.clickContinue();

	}

	@Test(description = "AE1_TC05 - Verify that a new user cannot sign-up with an already signedup email", groups = {
			"regression" })
	@Epic("Signup")
	@Feature("User Invalid Registration")
	@Story("Signup using existing email")
	@Severity(SeverityLevel.BLOCKER)
	@Owner("QE@Cloudyfolk")
	public void Test_AE1_TC05_Verify_Existing_Email_SignUp() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Sign-up Page
		logger.info("Running:Step-2");
		ReportManager.logStep("Navigate to Sign-Up page");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");
		signupLoginPage.verifyPageHeader("signup", "New User Signup!");

		// Step-3 Enter sign-up details with existing email and submit the form
		logger.info("Running:Step-3");
		ReportManager.logStep("On Successfull Navigation, entering the signup credentials");
		signupLoginPage.EnterSignupDetails("TestAccount1", "theqewheel@gmail.com");
		signupLoginPage.ClickSignup();
		signupLoginPage.verifyErrorForExistingEmailSignUp();

	}
}
