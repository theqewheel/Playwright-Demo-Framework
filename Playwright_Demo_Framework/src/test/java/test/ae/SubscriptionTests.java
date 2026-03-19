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

public class SubscriptionTests extends BaseTest{
	
	@Test(description = "AE7_TC10 - Verify subscription in Home Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Home Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC10_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Subscribe from Home
		logger.info("Running:Step-2");
		ReportManager.logStep("Subscribing to Portal");
		homePage.verifySubscriptionHeaderHomePage();
		homePage.subscribe("AutoTest1@cloudyfolk.com");
		homePage.waitUntilPageLoadCompletes();
		homePage.verifySubscriptionSuccessfromHomePage();
	}
	
	@Test(description = "AE7_TC11 - Verify subscription in Cart Page", groups = { "regression" })
	@Epic("Subscription")
	@Feature("Users can subscribe to the portal")
	@Story("Subscription can be made via Cart Page")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE7_TC10_Verify_Subscription_Cart_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.clickMenu("cart");
		cartPage.verifyPageLoaded("view_cart", "Checkout");

		// Step-2 Subscribe from Cart
		logger.info("Running:Step-2");
		ReportManager.logStep("Subscribing to Portal");
		cartPage.verifySubscriptionHeaderCartPage();
		cartPage.subscribe("AutoTest2@cloudyfolk.com");
		homePage.waitUntilPageLoadCompletes();
		cartPage.verifySubscriptionSuccessfromCartPage();
	}

}
