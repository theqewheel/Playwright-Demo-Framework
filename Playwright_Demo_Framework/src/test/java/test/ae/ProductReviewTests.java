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

public class ProductReviewTests extends BaseTest {

	@Test(description = "AE5_TC21 - Verify adding a product review", groups = { "smoke", "regression" })
	@Epic("Product Search")
	@Feature("Product Search Page for Products")
	@Story("Products can be searched and provided a review")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE5_TC21_Verify_Adding_Product_Review() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");

		// Step-2 Navigate to ALL Products Page
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");
		productsPage.verifyPageLoaded("/products", "All Products");

		// Step-3 Filter for a Brand
		logger.info("Running:Step-3");
		productsPage.clickViewProduct(1);
		productDetailPage.verifyWriteAReviewPage();
		productDetailPage.writeProductReviewAndSubmit("Autobot", "Autobot@test.com",
				"The product was excellent and durable, value for money!");
		productDetailPage.verifyReviewSubmissionSuccessMessage();
	}
}
