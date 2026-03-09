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
	@Story("Products can be searched and viewed in detail")
	@Severity(SeverityLevel.BLOCKER)
	@Owner("QE@Cloudyfolk")
	public void Test_AE4_TC08_Verify_Product_Search_Detail_Page() {

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

	@Test(description = "AE4_TC09 - Verify Search functionality shows all the Searched Products", groups = {
			"regression" })
	@Epic("Product Search")
	@Feature("Product Search Page for Products")
	@Story("Products can be searched and viewed in detail")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE4_TC09_Verify_Product_Search_Lists_All_Relevant_Products() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to ALL Products Page
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");
		productsPage.verifyPageLoaded("/products", "All Products");
		productsPage.verifyProductsListonLoad();

		// Step-3 Search for a Product
		logger.info("Running:Step-3");
		productsPage.searchProduct("Green");
		productsPage.verifySearchedProducts("Green");
	}

	@Test(description = "AE4_TC18 - Verify category filtering for products", groups = { "smoke", "regression" })
	@Epic("Product Search")
	@Feature("Product Search Page for Products")
	@Story("Products can be searched with filter - category")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE4_TC18_Verify_Product_Category_Search() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to ALL Products Page
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");
		productsPage.verifyPageLoaded("/products", "All Products");

		// Step-3 Search for a Category 
		logger.info("Running:Step-3");
		productsPage.clickSubCategory("WOMEN","DRESS");
		productsPage.verifyCategoryPageDisplay("WOMEN","DRESS");
		productsPage.clickSubCategory("MEN","JEANS");
		productsPage.verifyCategoryPageDisplay("MEN","JEANS");
		
		
	}

}
