package test.ae;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reporting.ReportManager;

public class AddToCartTests extends BaseTest{

	@Test(description = "AE7_TC10 - Verify subscription in Home Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Home Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC12_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Subscribe from Home
		logger.info("Running:Step-2");
		
	}
	
	@Test(description = "AE7_TC10 - Verify subscription in Home Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Home Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC13_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Subscribe from Home
		logger.info("Running:Step-2");
		
	}
	
	@Test(description = "AE7_TC10 - Verify subscription in Home Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Home Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC17_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Subscribe from Home
		logger.info("Running:Step-2");
		
	}
	
	@Test(description = "AE7_TC10 - Verify subscription in Home Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Home Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC20_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Subscribe from Home
		logger.info("Running:Step-2");
		
	}
	
	@Test(description = "AE7_TC10 - Verify subscription in Home Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Home Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC22_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Subscribe from Home
		logger.info("Running:Step-2");
		
	}
	
}
