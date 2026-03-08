package test;

import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import pages.ae.HomePage;
import pages.ae.ProductDetailPage;
import pages.ae.ProductsPage;
import reporting.ReportManager;

public class ProductsTests extends BaseTest {

	@Test(description = "AE4_TC08 - Verify the All Products and Product Details Page", groups = { "smoke",
			"regression" })
	@Epic("Product Search")
	@Feature("Product Search Page for Products")
	@Story("Proucts can be searched and viewed in detail")
	@Severity(SeverityLevel.BLOCKER)
	@Owner("QE@Cloudyfolk")
	public void Test_AE3_TC07_Verify_Product_Search_Detail_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to ALL Products Page
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");
		productsPage.verifyPageLoaded("/products", "All Products");
		productsPage.verifyProductsListonLoad();
		
		// Step-3 Navigate to Product Detail Page for the first Product displayed
		logger.info("Running:Step-3");
		productsPage.clickViewProduct(1);
		
		// Step-4 Verify the Product Details on the Product Detail Page
		logger.info("Running:Step-4");
		productDetailPage.verifyProductDetailsAreVisible();
	}
}
