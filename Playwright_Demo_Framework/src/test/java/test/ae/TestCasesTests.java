package test.ae;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import pages.ae.HomePage;
import pages.ae.TestCasesPage;
import reporting.ReportManager;

public class TestCasesTests extends BaseTest {

	@Test(description = "AE3_TC07 - Verify the Test Cases Page", groups = { "smoke", "regression" })
	@Epic("Test Cases")
	@Feature("Test Cases Page for Web App Functionality Tests")
	@Story("Test Cases Page for Web App is available for view")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE3_TC07_Verify_TestCases_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Test Cases Page
		logger.info("Running:Step-2");
		homePage.clickMenu("Test Cases");
		testCasesPage.verifyPageLoaded("/test_cases", "Test Cases");
	}
}
