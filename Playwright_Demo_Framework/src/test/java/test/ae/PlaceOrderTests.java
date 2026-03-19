package test.ae;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import org.testng.annotations.Test;

import base.BaseTest;
import framework.data.SignupData;
import framework.factory.SignupFactory;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import reporting.ReportManager;

public class PlaceOrderTests extends BaseTest {

	String username;
	String email;
	String password;

	@Test(description = "AE10_TC14 - Place order: Register while checkout", groups = { "regression" })
	@Epic("Place Order")
	@Feature("Order Placement through Cart Checkout")
	@Story("Order Placements for Customers")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE10_TC14_Verify_Place_Order_For_Customer_Registered_On_Checkout() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Add products to cart
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");
		int prodCount = 2; // update for more count of products
		Map<String, String> productDetails = productsPage.addMultipleProductsToCartAndRetreiveProductDetails(prodCount);

		// Step-3 Go to cart and proceed to checkout
		logger.info("Running:Step-3");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();

		// Step-4 Sign-up to checkout
		logger.info("Running:Step-4");
		cartPage.clickRegisterLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");

		SignupData data = fetchFakeDataForTest();

		signupLoginPage.EnterSignupDetails(data.getAccountInfo().getUsername(), data.getAccountInfo().getEmail());
		signupLoginPage.ClickSignup();
		signupDetailPage.enterFakerAccountInformation(data);
		signupDetailPage.enterFakerAddressInformation(data);
		signupDetailPage.clickCreateAccount();
		signupDetailPage.verifyTextMessageDisplayed("Account Created", false);
		signupDetailPage.clickContinue();
		homePage.verifyTextMessageDisplayed("Logged in as " + data.getAccountInfo().getUsername(), true);

		// Step-5 Navigate to Cart and proceed to checkout
		logger.info("Running:Step-5");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();
		cartPage.waitUntilPageLoadCompletes();
		cartPage.verifyPageLoaded("/checkout", "Checkout");

		// Step-6 Verify address details
		logger.info("Running:Step-6.1");
		Map<String, String> deliveryAddressOnCheckout = checkOutPage.fetchDeliveryAddress();
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine1"),
				(data.getPersonalInfo().getGender().equals("Male") ? "Mr" : "Mrs") + ". "
						+ data.getPersonalInfo().getFirstName() + " " + data.getPersonalInfo().getLastName(),
				"FullName with Title mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine2"), data.getAddressInfo().getCompany(),
				"Company Name mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine3"), data.getAddressInfo().getAddress1(),
				"Address1 mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine4"), data.getAddressInfo().getAddress2(),
				"Address2 mismatched");
		softAssert.assertEquals(
				deliveryAddressOnCheckout.get("AddressLine5"), data.getAddressInfo().getCity() + " "
						+ data.getAddressInfo().getState() + " " + data.getAddressInfo().getZip(),
				"City State Zipcode mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine6"), data.getAddressInfo().getCountry(),
				"Country mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine7"), data.getAddressInfo().getPhoneNumber(),
				"Phone mismatched");

		Map<String, String> invoiceAddressOnCheckout = checkOutPage.fetchInvoiceAddress();

		// Verify delivery and invoice address matches
		softAssert.assertEquals(invoiceAddressOnCheckout, deliveryAddressOnCheckout,
				"The invoice and delivery address on Check-out are not equal!");

		// Step 6 Review Order Details
		logger.info("Running:Step-6.2");
		int calculatedCartTotals = 0;
		Map<String, String> cartProductDetails = checkOutPage.readAllCartProductDetails();
		for (int i = 1; i <= prodCount; i++) {
			softAssert.assertEquals(cartProductDetails.get("Name" + i), productDetails.get("Name" + i),
					"Name mismatched");
			softAssert.assertEquals(cartProductDetails.get("Price" + i), productDetails.get("Price" + i),
					"Price mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Quantity" + i)), 1, "Quantity mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Total" + i)),
					Integer.parseInt(productDetails.get("Price" + i)), "Total mismatched");
			calculatedCartTotals += Integer.parseInt(productDetails.get("Price" + i));
		}

		logger.info("The expected cart total: " + calculatedCartTotals);

		/*
		 * verify Cart Overall total (additional check done - optional)
		 */

		softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Overall Total")), calculatedCartTotals,
				"Overall Cart Total mismatched");

		// Step-7 Enter Comment and Place Order
		logger.info("Running:Step-7");
		checkOutPage.enterCheckoutComment(" ");
		checkOutPage.clickPlaceOrder();

		// Step-8 Enter Payment Details
		logger.info("Running:Step-8");
		checkOutPage.fillFakerPaymentDetails(data);

		// Step-9 Pay and Confirm
		logger.info("Running:Step-9");

		/*
		 * Set up dialog/response listener before clicking to verify the place order
		 * success message which displays and goes off on transition to payment success
		 * page
		 */
		page.onResponse(response -> {
			if (page.getByText("order has been placed").count() > 0) {
				checkOutPage.verifyTextMessageDisplayed("Your order has been placed successfully!", true);
				captureScreenshot("Order placement message");
			}
		});

		checkOutPage.clickPayAndConfirmOrder();

		// Step-10 Verify Order Payment Confirmation
		logger.info("Running:Step-10");
		checkOutPage.verifyTextMessageDisplayed("Your order has been confirmed!", true);

		// Step-11 Delete Account (optional:best practice)
		logger.info("Running:Step-11 >>>> Cleanup");
		homePage.clickDeleteAccount();
		homePage.clickContinue();

	}

	@Test(description = "AE10_TC15 - Place order: Register before checkout", groups = { "regression" })
	@Epic("Place Order")
	@Feature("Order Placement through Cart Checkout")
	@Story("Order Placements for Customers")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE10_TC15_Verify_Place_Order_For_Customer_Registered_Before_Checkout() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Sign-up to checkout
		logger.info("Running:Step-2");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");

		SignupData data = fetchFakeDataForTest();

		signupLoginPage.EnterSignupDetails(data.getAccountInfo().getUsername(), data.getAccountInfo().getEmail());
		signupLoginPage.ClickSignup();
		signupDetailPage.enterFakerAccountInformation(data);
		signupDetailPage.enterFakerAddressInformation(data);
		signupDetailPage.clickCreateAccount();
		signupDetailPage.verifyTextMessageDisplayed("Account Created", false);
		signupDetailPage.clickContinue();
		homePage.verifyTextMessageDisplayed("Logged in as " + data.getAccountInfo().getUsername(), true);

		// Step-3 Add products to cart
		logger.info("Running:Step-3");
		homePage.clickMenu("Products");
		List<String> productsToOrderList = List.of("Sleeveless", "Unicorn", "wINter", "Panda");
		int prodCount = productsToOrderList.size(); // update for more count of products
		Map<String, String> productDetails = productsPage
				.addMultipleProductsToCartAndRetreiveProductDetails(productsToOrderList);

		// Step-4 Navigate to Cart and proceed to checkout
		logger.info("Running:Step-4");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();
		cartPage.waitUntilPageLoadCompletes();
		cartPage.verifyPageLoaded("/checkout", "Checkout");

		// Step-5 Verify address details
		logger.info("Running:Step-5.1");
		Map<String, String> deliveryAddressOnCheckout = checkOutPage.fetchDeliveryAddress();
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine1"),
				(data.getPersonalInfo().getGender().equals("Male") ? "Mr" : "Mrs") + ". "
						+ data.getPersonalInfo().getFirstName() + " " + data.getPersonalInfo().getLastName(),
				"FullName with Title mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine2"), data.getAddressInfo().getCompany(),
				"Company Name mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine3"), data.getAddressInfo().getAddress1(),
				"Address1 mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine4"), data.getAddressInfo().getAddress2(),
				"Address2 mismatched");
		softAssert.assertEquals(
				deliveryAddressOnCheckout.get("AddressLine5"), data.getAddressInfo().getCity() + " "
						+ data.getAddressInfo().getState() + " " + data.getAddressInfo().getZip(),
				"City State Zipcode mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine6"), data.getAddressInfo().getCountry(),
				"Country mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine7"), data.getAddressInfo().getPhoneNumber(),
				"Phone mismatched");

		Map<String, String> invoiceAddressOnCheckout = checkOutPage.fetchInvoiceAddress();

		// Verify delivery and invoice address matches
		softAssert.assertEquals(invoiceAddressOnCheckout, deliveryAddressOnCheckout,
				"The invoice and delivery address on Check-out are not equal!");

		// Step 5 Review Order Details
		logger.info("Running:Step-5.2");
		int calculatedCartTotals = 0;
		Map<String, String> cartProductDetails = checkOutPage.readAllCartProductDetails();
		for (int i = 1; i <= prodCount; i++) {
			softAssert.assertEquals(cartProductDetails.get("Name" + i), productDetails.get("Name" + i),
					"Name mismatched");
			softAssert.assertEquals(cartProductDetails.get("Price" + i), productDetails.get("Price" + i),
					"Price mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Quantity" + i)), 1, "Quantity mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Total" + i)),
					Integer.parseInt(productDetails.get("Price" + i)), "Total mismatched");
			calculatedCartTotals += Integer.parseInt(productDetails.get("Price" + i));
		}

		logger.info("The expected cart total: " + calculatedCartTotals);

		/*
		 * verify Cart Overall total (additional check done - optional)
		 */

		softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Overall Total")), calculatedCartTotals,
				"Overall Cart Total mismatched");

		// Step-7 Enter Comment and Place Order
		logger.info("Running:Step-7");
		checkOutPage.enterCheckoutComment("This order is a test order only.");
		checkOutPage.clickPlaceOrder();

		// Step-8 Enter Payment Details
		logger.info("Running:Step-8");
		checkOutPage.fillFakerPaymentDetails(data);

		// Step-9 Pay and Confirm
		logger.info("Running:Step-9");
		/*
		 * Set up dialog/response listener before clicking to verify the place order
		 * success message which displays and goes off on transition to payment success
		 * page
		 */
		page.onResponse(response -> {
			if (page.getByText("order has been placed").count() > 0) {
				checkOutPage.verifyTextMessageDisplayed("Your order has been placed successfully!", true);
			}
		});

		checkOutPage.clickPayAndConfirmOrder();

		// Step-10 Verify Order Payment Confirmation
		logger.info("Running:Step-10");
		checkOutPage.verifyTextMessageDisplayed("Your order has been confirmed!", true);

		// Step-11 Delete Account (optional:best practice)
		logger.info("Running:Step-11 >>>> Cleanup");
		homePage.clickDeleteAccount();
		homePage.clickContinue();
	}

	@Test(description = "AE10_TC16 - Place order: Login before checkout", groups = { "regression" })
	@Epic("Place Order")
	@Feature("Order Placement through Cart Checkout")
	@Story("Order Placements for Customers")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE10_TC16_Verify_Place_Order_LoggedIn_Before_Checkout() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-1.1 Sign-up to login (optional - but needed for having a valid user
		// account for test each run)
		logger.info("Running:Step-1.1 >>>> Pre-Data set up for account login");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");

		SignupData data = fetchFakeDataForTest();

		signupLoginPage.EnterSignupDetails(data.getAccountInfo().getUsername(), data.getAccountInfo().getEmail());
		signupLoginPage.ClickSignup();
		signupDetailPage.enterFakerAccountInformation(data);
		signupDetailPage.enterFakerAddressInformation(data);
		signupDetailPage.clickCreateAccount();
		signupDetailPage.verifyTextMessageDisplayed("Account Created", false);
		signupDetailPage.clickContinue();
		homePage.verifyTextMessageDisplayed("Logged in as " + data.getAccountInfo().getUsername(), true);
		homePage.clickLogOut();
		
		// Step-2 Login to account (You can supply a valid credential here -- not
		// recommended though!)
		logger.info("Running:Step-2");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");
		signupLoginPage.EnterLoginDetails(data.getAccountInfo().getEmail(), data.getAccountInfo().getPassword());
		signupLoginPage.ClickLogin();

		// Step-3 Add products to cart
		logger.info("Running:Step-3");
		homePage.clickMenu("Products");
		List<String> productsToOrderList = List.of("Sleeveless", "Unicorn", "wINter", "Panda");
		int prodCount = productsToOrderList.size(); // update for more count of products
		Map<String, String> productDetails = productsPage
				.addMultipleProductsToCartAndRetreiveProductDetails(productsToOrderList);

		// Step-4 Navigate to Cart and proceed to checkout
		logger.info("Running:Step-4");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();
		cartPage.waitUntilPageLoadCompletes();
		cartPage.verifyPageLoaded("/checkout", "Checkout");

		// Step-5 Verify address details
		logger.info("Running:Step-5.1");
		Map<String, String> deliveryAddressOnCheckout = checkOutPage.fetchDeliveryAddress();
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine1"),
				(data.getPersonalInfo().getGender().equals("Male") ? "Mr" : "Mrs") + ". "
						+ data.getPersonalInfo().getFirstName() + " " + data.getPersonalInfo().getLastName(),
				"FullName with Title mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine2"), data.getAddressInfo().getCompany(),
				"Company Name mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine3"), data.getAddressInfo().getAddress1(),
				"Address1 mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine4"), data.getAddressInfo().getAddress2(),
				"Address2 mismatched");
		softAssert.assertEquals(
				deliveryAddressOnCheckout.get("AddressLine5"), data.getAddressInfo().getCity() + " "
						+ data.getAddressInfo().getState() + " " + data.getAddressInfo().getZip(),
				"City State Zipcode mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine6"), data.getAddressInfo().getCountry(),
				"Country mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine7"), data.getAddressInfo().getPhoneNumber(),
				"Phone mismatched");

		Map<String, String> invoiceAddressOnCheckout = checkOutPage.fetchInvoiceAddress();

		// Verify delivery and invoice address matches
		softAssert.assertEquals(invoiceAddressOnCheckout, deliveryAddressOnCheckout,
				"The invoice and delivery address on Check-out are not equal!");

		// Step 5 Review Order Details
		logger.info("Running:Step-5.2");
		int calculatedCartTotals = 0;
		Map<String, String> cartProductDetails = checkOutPage.readAllCartProductDetails();
		for (int i = 1; i <= prodCount; i++) {
			softAssert.assertEquals(cartProductDetails.get("Name" + i), productDetails.get("Name" + i),
					"Name mismatched");
			softAssert.assertEquals(cartProductDetails.get("Price" + i), productDetails.get("Price" + i),
					"Price mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Quantity" + i)), 1, "Quantity mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Total" + i)),
					Integer.parseInt(productDetails.get("Price" + i)), "Total mismatched");
			calculatedCartTotals += Integer.parseInt(productDetails.get("Price" + i));
		}

		logger.info("The expected cart total: " + calculatedCartTotals);

		/*
		 * verify Cart Overall total (additional check done - optional)
		 */

		softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Overall Total")), calculatedCartTotals,
				"Overall Cart Total mismatched");

		// Step-7 Enter Comment and Place Order
		logger.info("Running:Step-7");
		checkOutPage.enterCheckoutComment("This order is a test order only.");
		checkOutPage.clickPlaceOrder();

		// Step-8 Enter Payment Details
		logger.info("Running:Step-8");
		checkOutPage.fillFakerPaymentDetails(data);

		// Step-9 Pay and Confirm
		logger.info("Running:Step-9");
		/*
		 * Set up dialog/response listener before clicking to verify the place order
		 * success message which displays and goes off on transition to payment success
		 * page
		 */
		page.onResponse(response -> {
			if (page.getByText("order has been placed").count() > 0) {
				checkOutPage.verifyTextMessageDisplayed("Your order has been placed successfully!", true);
				captureScreenshot("Order placement message");
			}
		});

		checkOutPage.clickPayAndConfirmOrder();

		// Step-10 Verify Order Payment Confirmation
		logger.info("Running:Step-10");
		checkOutPage.verifyTextMessageDisplayed("Your order has been confirmed!", true);

		// Step-11 Delete Account (optional:best practice)
		logger.info("Running:Step-11 >>>> Cleanup");
		homePage.clickDeleteAccount();
		homePage.clickContinue();
	}

	@Test(description = "AE10_TC23 - Verify address on checkout", groups = { "regression" })
	@Epic("Place Order")
	@Feature("Order Placement through Cart Checkout")
	@Story("Order Placements for Customers")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE10_TC23_Verify_Address_On_Checkout() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Sign-up to checkout
		logger.info("Running:Step-2");
		homePage.clickSignupLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");

		SignupData data = fetchFakeDataForTest();

		signupLoginPage.EnterSignupDetails(data.getAccountInfo().getUsername(), data.getAccountInfo().getEmail());
		signupLoginPage.ClickSignup();
		signupDetailPage.enterFakerAccountInformation(data);
		signupDetailPage.enterFakerAddressInformation(data);
		signupDetailPage.clickCreateAccount();
		signupDetailPage.verifyTextMessageDisplayed("Account Created", false);
		signupDetailPage.clickContinue();
		homePage.verifyTextMessageDisplayed("Logged in as " + data.getAccountInfo().getUsername(), true);

		// Step-3 Add products to cart
		logger.info("Running:Step-3");
		homePage.clickMenu("Products");
		int prodCount = 2; // update for more count of products
		productsPage.addMultipleProductsToCartAndRetreiveProductDetails(prodCount);

		// Step-4 Navigate to Cart and proceed to checkout
		logger.info("Running:Step-4");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();
		cartPage.waitUntilPageLoadCompletes();
		cartPage.verifyPageLoaded("/checkout", "Checkout");

		// Step-5 Verify delivery address details is same as that provided during
		// sign-up
		logger.info("Running:Step-5");
		Map<String, String> deliveryAddressOnCheckout = checkOutPage.fetchDeliveryAddress();
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine1"),
				(data.getPersonalInfo().getGender().equals("Male") ? "Mr" : "Mrs") + ". "
						+ data.getPersonalInfo().getFirstName() + " " + data.getPersonalInfo().getLastName(),
				"FullName with Title mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine2"), data.getAddressInfo().getCompany(),
				"Company Name mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine3"), data.getAddressInfo().getAddress1(),
				"Address1 mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine4"), data.getAddressInfo().getAddress2(),
				"Address2 mismatched");
		softAssert.assertEquals(
				deliveryAddressOnCheckout.get("AddressLine5"), data.getAddressInfo().getCity() + " "
						+ data.getAddressInfo().getState() + " " + data.getAddressInfo().getZip(),
				"City State Zipcode mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine6"), data.getAddressInfo().getCountry(),
				"Country mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine7"), data.getAddressInfo().getPhoneNumber(),
				"Phone mismatched");

		Map<String, String> invoiceAddressOnCheckout = checkOutPage.fetchInvoiceAddress();

		// Step-7 Verify invoice address details is same as that provided during sign-up
		// Verify delivery and invoice address matches
		logger.info("Running:Step-7");
		softAssert.assertEquals(invoiceAddressOnCheckout, deliveryAddressOnCheckout,
				"The invoice and delivery address on Check-out are not equal!");

		// Step-8 Delete Account (optional:best practice)
		logger.info("Running:Step-8 >>>> Cleanup");
		homePage.clickDeleteAccount();
		homePage.clickContinue();
	}

	@Test(description = "AE10_TC24 - Verify Purchase order Invoice download", groups = { "regression" })
	@Epic("Place Order")
	@Feature("Order Placement through Cart Checkout")
	@Story("Order Placements for Customers")
	@Severity(SeverityLevel.CRITICAL)
	@Owner("QE@Cloudyfolk")
	public void Test_AE10_TC24_Verify_Purchase_Order_Invoice_Download() {

		// Step-1 Navigate to the URL
		logger.info("Running:Step-1");
		ReportManager.logStep("Navigating to Automation Exercise");
		homePage.verifyPageLoaded("automationexercise", "Automation Exercise");

		// Step-2 Add products to cart
		logger.info("Running:Step-2");
		homePage.clickMenu("Products");
		int prodCount = 2; // update for more count of products
		Map<String, String> productDetails = productsPage.addMultipleProductsToCartAndRetreiveProductDetails(prodCount);

		// Step-3 Go to cart and proceed to checkout
		logger.info("Running:Step-3");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();

		// Step-4 Sign-up to checkout
		logger.info("Running:Step-4");
		cartPage.clickRegisterLoginLink();
		signupLoginPage.verifyPageLoaded("/login", "Signup");

		SignupData data = fetchFakeDataForTest();

		signupLoginPage.EnterSignupDetails(data.getAccountInfo().getUsername(), data.getAccountInfo().getEmail());
		signupLoginPage.ClickSignup();
		signupDetailPage.enterFakerAccountInformation(data);
		signupDetailPage.enterFakerAddressInformation(data);
		signupDetailPage.clickCreateAccount();
		signupDetailPage.verifyTextMessageDisplayed("Account Created", false);
		signupDetailPage.clickContinue();
		homePage.verifyTextMessageDisplayed("Logged in as " + data.getAccountInfo().getUsername(), true);

		// Step-5 Navigate to Cart and proceed to checkout
		logger.info("Running:Step-5");
		homePage.clickMenu("Cart");
		cartPage.clickProceedToCheckout();
		cartPage.waitUntilPageLoadCompletes();
		cartPage.verifyPageLoaded("/checkout", "Checkout");

		// Step-6 Verify address details
		logger.info("Running:Step-6.1");
		Map<String, String> deliveryAddressOnCheckout = checkOutPage.fetchDeliveryAddress();
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine1"),
				(data.getPersonalInfo().getGender().equals("Male") ? "Mr" : "Mrs") + ". "
						+ data.getPersonalInfo().getFirstName() + " " + data.getPersonalInfo().getLastName(),
				"FullName with Title mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine2"), data.getAddressInfo().getCompany(),
				"Company Name mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine3"), data.getAddressInfo().getAddress1(),
				"Address1 mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine4"), data.getAddressInfo().getAddress2(),
				"Address2 mismatched");
		softAssert.assertEquals(
				deliveryAddressOnCheckout.get("AddressLine5"), data.getAddressInfo().getCity() + " "
						+ data.getAddressInfo().getState() + " " + data.getAddressInfo().getZip(),
				"City State Zipcode mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine6"), data.getAddressInfo().getCountry(),
				"Country mismatched");
		softAssert.assertEquals(deliveryAddressOnCheckout.get("AddressLine7"), data.getAddressInfo().getPhoneNumber(),
				"Phone mismatched");

		Map<String, String> invoiceAddressOnCheckout = checkOutPage.fetchInvoiceAddress();

		// Verify delivery and invoice address matches
		softAssert.assertEquals(invoiceAddressOnCheckout, deliveryAddressOnCheckout,
				"The invoice and delivery address on Check-out are not equal!");

		// Step 6 Review Order Details
		logger.info("Running:Step-6.2");
		int calculatedCartTotals = 0;
		Map<String, String> cartProductDetails = checkOutPage.readAllCartProductDetails();
		for (int i = 1; i <= prodCount; i++) {
			softAssert.assertEquals(cartProductDetails.get("Name" + i), productDetails.get("Name" + i),
					"Name mismatched");
			softAssert.assertEquals(cartProductDetails.get("Price" + i), productDetails.get("Price" + i),
					"Price mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Quantity" + i)), 1, "Quantity mismatched");
			softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Total" + i)),
					Integer.parseInt(productDetails.get("Price" + i)), "Total mismatched");
			calculatedCartTotals += Integer.parseInt(productDetails.get("Price" + i));
		}

		logger.info("The expected cart total: " + calculatedCartTotals);

		/*
		 * verify Cart Overall total (additional check done - optional)
		 */

		softAssert.assertEquals(Integer.parseInt(cartProductDetails.get("Overall Total")), calculatedCartTotals,
				"Overall Cart Total mismatched");

		// Step-7 Enter Comment and Place Order
		logger.info("Running:Step-7");
		checkOutPage.enterCheckoutComment(" ");
		checkOutPage.clickPlaceOrder();

		// Step-8 Enter Payment Details
		logger.info("Running:Step-8");
		checkOutPage.fillFakerPaymentDetails(data);

		// Step-9 Pay and Confirm
		logger.info("Running:Step-9");

		/*
		 * Set up dialog/response listener before clicking to verify the place order
		 * success message which displays and goes off on transition to payment success
		 * page
		 */
		page.onResponse(response -> {
			if (page.getByText("order has been placed").count() > 0) {
				checkOutPage.verifyTextMessageDisplayed("Your order has been placed successfully!", true);
			}
		});

		checkOutPage.clickPayAndConfirmOrder();

		// Step-10 Verify Order Payment Confirmation
		logger.info("Running:Step-10");
		checkOutPage.verifyTextMessageDisplayed("Your order has been confirmed!", true);

		// Step-11 Verify invoice download
		logger.info("Running:Step-11");
		Path invoicePath = checkOutPage.downloadInvoice();

		checkOutPage.verifyInvoiceContent(invoicePath, data.getPersonalInfo().getFirstName(),
				data.getPersonalInfo().getLastName(), 
				String.valueOf(calculatedCartTotals));
	
	}

	/**
	 * HELPER METHOD
	 */

	public SignupData fetchFakeDataForTest() {
		var data = SignupFactory.createSignupData();
		return data;
	}

}
