package test.ae;

import static org.testng.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.Test;

import base.BaseTest;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reporting.ReportManager;

public class AddToCartTests extends BaseTest {

	@Test(description = "AE9_TC12 - Verify Add Products In Cart", groups = { "regression" })
	@Epic("Add To Cart")
	@Feature("Carts can be updated")
	@Story("Add products in Cart")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE9_TC12_Verify_Add_Products_In_Cart() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Adding products to cart
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");

		Map<String, String> ProductDetails;
		int prodCount = 2; // update for more count of products

		ProductDetails = productsPage.addMultipleProductsToCartAndRetreiveProductDetails(prodCount);

		// Step-3 Verify the products added to the cart
		logger.info("Running:Step-3");

		Map<String, String> cartProductDetails = new HashMap<String, String>();

		for (int i = 1; i <= prodCount; i++) {
			cartProductDetails = cartPage.readCartProductDetails(i);
			softAssert.assertEquals(cartProductDetails.get("Name" + i), ProductDetails.get("Name"), "Name mismatched");
			softAssert.assertEquals(cartProductDetails.get("Price" + i), ProductDetails.get("Price"),
					"Price mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Quantity")), 1, "Quantity mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Total")),
					Integer.parseInt(ProductDetails.get("Price" + i)), "Total mismatched");
		}
	}

	@Test(description = "AE9_TC13 - Verify product quantity in cart", groups = { "regression" })
	@Epic("Add To Cart")
	@Feature("Carts can be updated")
	@Story("Update products in Cart")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE9_TC13_Verify_Subscription_Home_Page() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Adding Product to cart with increased Quantity (enhanced test for
		// adding multiple products)
		logger.info("Running:Step-2");
		List<String> productNames = List.of("Sleeveless", "Stylish", "Summer White Top", "Blue Top");
		int quantity = 4; // control the quantity
		productsPage.addMultipleProductsToCart(productNames);

		// Step-3 Viewing the product and updating quantity
		logger.info("Running:Step-3");
		String productName = "Sleeveless";
		productsPage.clickViewProduct(productName);
		productDetailPage.increaseProductPurchaseQuantity(quantity);
		productDetailPage.addToCart();

		// Step-4 Verify the cart with updated quantity
		logger.info("Running:Step-4");
		productDetailPage.clickViewCart();
		int cartQty = Integer.parseInt(cartPage.readCartProductDetails("Sleeveless").get("Quantity"));
		Assert.assertEquals(cartQty, quantity, "The quantity is not matching for the Product");

	}

	@Test(description = "AE9_TC17 - Verify removal of products in cart", groups = { "regression" })
	@Epic("Add To Cart")
	@Feature("Carts can be updated")
	@Story("Remove products in Cart")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE9_TC17_Verify_Removal_From_Cart() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Add Products to Cart
		logger.info("Running:Step-2");
		productsPage.addMultipleProductsToCart(5);
		productsPage.clickAddProductToCart("Frozen");
		productsPage.clickContinueShopping();

		// Step-3 Remove Products from Cart
		logger.info("Running:Step-3");
		homePage.clickMenu("Cart");
		cartPage.removeProductFromCart(1);
		String productToRemove = "Frozen";
		cartPage.removeProductFromCart(productToRemove);
		Assert.assertFalse(cartPage.verifyProductExistanceInCart(productToRemove),
				"The Product - " + productToRemove + " is still visible on Cart.");

		// Step-4 Remove all products in cart (Optional Test)
		logger.info("Running:Step-4");
		cartPage.removeAllProductsFromCart();
	}

	@Test(description = "AE9_TC20 - Verify added products in cart after login", groups = { "regression" })
	@Epic("Add To Cart")
	@Feature("Carts can be updated")
	@Story("Add products in Cart")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE9_TC20_Verify_Cart_On_Login() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Navigate to Products and Search for Product
		logger.info("Running:Step-2");
		String searchProduct = "Sleeveless";
		homePage.clickMenu("Products");
		productsPage.searchProduct(searchProduct);
		productsPage.verifySearchedProducts(searchProduct);

		// Step-3 Add searched product to Cart and verify the cart
		logger.info("Running:Step-3");
		List<String> productList = productsPage.addAllProductsVisibleToCart();
		for (String p : productList) {
			Assert.assertTrue(cartPage.verifyProductExistanceInCart(p),
					"The Product - " + p + " is visible on Cart.");
		}

		// Step-4 Login to application and verify the cart
		logger.info("Running:Step-4");
		homePage.clickSignupLoginLink();
		signupLoginPage.EnterLoginDetails("theqewheel@gmail.com", "admin@123");
		signupLoginPage.ClickLogin();
		homePage.clickMenu("Cart");
		for (String p : productList) {
			Assert.assertTrue(cartPage.verifyProductExistanceInCart(p),
					"The Product - " + p + " is visible on Cart after Login.");
		}
	}

	@Test(description = "AE9_TC22 - Verify adding recommended items to cart", groups = { "regression" })
	@Epic("Add To Cart")
	@Feature("Carts can be updated")
	@Story("Add products in Cart")
	@Severity(SeverityLevel.NORMAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE9_TC22_Verify_Add_Recommended_To_Cart() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Add recommended product to cart
		logger.info("Running:Step-2");
		String searchProduct = "Blue Top";
		productsPage.addRecommendedProductToCart(searchProduct);
		productsPage.clickViewCart();
		Assert.assertTrue(cartPage.verifyProductExistanceInCart(searchProduct),
				"The Product - " + searchProduct + " is visible on Cart.");
	}

}
