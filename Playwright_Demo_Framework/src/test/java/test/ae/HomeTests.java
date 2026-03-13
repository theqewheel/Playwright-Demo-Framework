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

public class HomeTests extends BaseTest {

	@Test(description = "AE8_TC25 - Verify scroll functionality with Arrow", groups = { "regression" })
	@Epic("Home")
	@Feature("Home Page for Users")
	@Story("Home Page - Usability with Scroll")
	@Severity(SeverityLevel.MINOR)
	@Owner("QE@Cloudyfolk")
	public void Test_AE8_TC25_Verify_Home_Page_Scroll_With_Arrow() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Scroll Action
		logger.info("Running:Step-2");
		homePage.scrollToPageBottomUsingKeyboard();
		homePage.verifySubscriptionHeaderVisibility();
		homePage.scrollToPageTopUsingArrowKey();
		homePage.verifyTextMessageDisplayed("Full-Fledged practice website for Automation Engineers", true);

	}

	@Test(description = "AE8_TC26 - Verify scroll functionality without Arrow", groups = { "regression" })
	@Epic("Home")
	@Feature("Home Page for Users")
	@Story("Home Page - Usability with Scroll")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE8_TC26_Verify_Home_Page_Scroll_Without_Arrow() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Scroll Action
		logger.info("Running:Step-2");
		homePage.scrollToPageBottomUsingEval();
		//homePage.scrollToPageBottomUsingKeyboard();
		homePage.verifySubscriptionHeaderVisibility();
		//homePage.scrollToPageTopUsingKeyboard();
		homePage.scrollToPageTopUsingEval();
		homePage.verifyTextMessageDisplayed("Full-Fledged practice website for Automation Engineers", true);
	}
}
