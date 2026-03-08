package test;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import pages.ae.ContactUsPage;
import pages.ae.HomePage;
import reporting.ReportManager;

public class ContactUsTests extends BaseTest {

	@Test(description = "AE3_TC06 - Verify Contact us forms can be submitted", groups = { "regression" })
	@Epic("Contact Us")
	@Feature("Contact Us Form")
	@Story("Contact Us Form can be submitted")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE3_TC06_Verify_Contact_Us_Form_Submission_Guest_User() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");
		
		// Step-2 Navigate to Contact Us
		logger.info("Running:Step-2");
		homePage.clickMenu("Contact us");
		homePage.verifyPageLoaded("/contact_us", "Contact Us");
		contactUsPage.verifyPageHeader1();
		contactUsPage.verifyPageHeader2();

		//Step-3 Fill the Contact Form and Submit
		logger.info("Running:Step-3");
		contactUsPage.fillForm("Autobot",
				"autobot@gmail.com",
				"Query on New Test Cases for Performance",
				"Hi There, Can you also add few test cases for performance testing the application.");
		contactUsPage.uploadFileFromLocalDirectory("C:\\Users\\theqe\\Downloads\\Science.pdf");
		contactUsPage.uploadFileFromProjectDirectory("test1.txt");
		contactUsPage.clickSubmit();
		contactUsPage.verifySuccessMessage();
		
		//Step-4 Navigate to Home Page
		logger.info("Running:Step-4");
		contactUsPage.clickHome();
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");
		
		
	}

}
