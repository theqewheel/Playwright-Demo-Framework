package pages.ae;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Download;
import com.microsoft.playwright.ElementHandle.ClickOptions;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import framework.data.SignupData;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import reporting.ReportManager;

public class CheckOutPage extends BasePage {

	private final Locator deliveryAddress;
	private final Locator invoiceAddress;
	private final String addressLineFullnameTitleString;
	private final String addressLineLocatorString;
	private final String addressLineCityStatePostcodeLocatorString;
	private final String addressLineCountryLocatorString;
	private final String addressLinePhoneLocatorString;
	private final Locator checkoutComment;
	private final Locator placeOrderButton;
	private final Locator cardPayeeName;
	private final Locator cardNumber;
	private final Locator cardCvc;
	private final Locator cardExpirationMonth;
	private final Locator cardExpirationYear;
	private final Locator payAndConfirmButton;
	private final Locator downloadInvoiceButton;
	private final Locator cartTable;

	public CheckOutPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		this.deliveryAddress = page.locator("ul#address_delivery");
		this.invoiceAddress = page.locator("ul#address_invoice");
		this.addressLineFullnameTitleString = ".address_firstname.address_lastname";
		this.addressLineLocatorString = ".address_address1.address_address2";
		this.addressLineCityStatePostcodeLocatorString = ".address_city.address_state_name.address_postcode";
		this.addressLineCountryLocatorString = ".address_country_name";
		this.addressLinePhoneLocatorString = ".address_phone";
		this.checkoutComment = page.locator("#ordermsg textarea");
		this.placeOrderButton = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Place Order"));
		this.cardPayeeName = page.getByTestId("name-on-card");
		this.cardNumber = page.getByTestId("card-number");
		this.cardCvc = page.getByTestId("cvc");
		this.cardExpirationMonth = page.getByTestId("expiry-month");
		this.cardExpirationYear = page.getByTestId("expiry-year");
		this.payAndConfirmButton = page.getByTestId("pay-button");
		this.downloadInvoiceButton = page.getByRole(AriaRole.LINK,
				new Page.GetByRoleOptions().setName("Download Invoice"));
		this.cartTable = page.locator("#cart_info");
	}

	@Step("Read delivery address displayed on checkout page")
	public Map<String, String> fetchDeliveryAddress() {

		Map<String, String> deliveryAddressMap = new HashMap<String, String>();

		String addressline1 = deliveryAddress.locator(addressLineFullnameTitleString).textContent().trim();

		String addressline2 = deliveryAddress.locator(addressLineLocatorString).nth(0).textContent().trim();

		String addressline3 = deliveryAddress.locator(addressLineLocatorString).nth(1).textContent().trim();

		String addressline4 = deliveryAddress.locator(addressLineLocatorString).nth(2).textContent().trim();

		String addressline5 = deliveryAddress.locator(addressLineCityStatePostcodeLocatorString).textContent().trim()
				.replaceAll("\\s+", " "); // replace multiple spaces/tabs/newlines with single space

		String addressline6 = deliveryAddress.locator(addressLineCountryLocatorString).textContent().trim();

		String addressline7 = deliveryAddress.locator(addressLinePhoneLocatorString).textContent().trim();

		deliveryAddressMap.put("AddressLine1", addressline1);
		deliveryAddressMap.put("AddressLine2", addressline2);
		deliveryAddressMap.put("AddressLine3", addressline3);
		deliveryAddressMap.put("AddressLine4", addressline4);
		deliveryAddressMap.put("AddressLine5", addressline5);
		deliveryAddressMap.put("AddressLine6", addressline6);
		deliveryAddressMap.put("AddressLine7", addressline7);

		logger.info("""

				-------------------------------------------------------------------------
				               DELIVERY ADDRESS ON CHECKOUT
				-------------------------------------------------------------------------
				Title Fullname  : {}
				Company Name    : {}
				Address Line1   : {}
				Address Line2   : {}
				Address Line3   : {}
				Country         : {}
				Phone           : {}
				-------------------------------------------------------------------------
				""", addressline1, addressline2, addressline3, addressline4, addressline5, addressline6, addressline7);

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();

		// Add Invoice summary section
		allureReport.append(String.format("""

				-------------------------------------------------------------------------
				               DELIVERY ADDRESS ON CHECKOUT
				-------------------------------------------------------------------------
				Title Fullname  : %s
				Company Name    : %s
				Address Line1   : %s
				Address Line2   : %s
				Address Line3   : %s
				Country         : %s
				Phone           : %s
				-------------------------------------------------------------------------
				""", addressline1, addressline2, addressline3, addressline4, addressline5, addressline6, addressline7));

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Checkout-Delivery Address Details", allureReport.toString());

		captureScreenshot();
		
		return deliveryAddressMap;

	}

	@Step("Read invoice address displayed on checkout page")
	public Map<String, String> fetchInvoiceAddress() {

		Map<String, String> invoiceAddressMap = new HashMap<String, String>();

		String addressline1 = invoiceAddress.locator(addressLineFullnameTitleString).textContent().trim();

		String addressline2 = invoiceAddress.locator(addressLineLocatorString).nth(0).textContent().trim();

		String addressline3 = invoiceAddress.locator(addressLineLocatorString).nth(1).textContent().trim();

		String addressline4 = invoiceAddress.locator(addressLineLocatorString).nth(2).textContent().trim();

		String addressline5 = invoiceAddress.locator(addressLineCityStatePostcodeLocatorString).textContent()
				.replaceAll("\\s+", " ").trim(); // replace multiple spaces/tabs/newlines with single space

		String addressline6 = invoiceAddress.locator(addressLineCountryLocatorString).textContent().trim();

		String addressline7 = invoiceAddress.locator(addressLinePhoneLocatorString).textContent().trim();

		invoiceAddressMap.put("AddressLine1", addressline1);
		invoiceAddressMap.put("AddressLine2", addressline2);
		invoiceAddressMap.put("AddressLine3", addressline3);
		invoiceAddressMap.put("AddressLine4", addressline4);
		invoiceAddressMap.put("AddressLine5", addressline5);
		invoiceAddressMap.put("AddressLine6", addressline6);
		invoiceAddressMap.put("AddressLine7", addressline7);

		logger.info("""

				-------------------------------------------------------------------------
				               INVOICE ADDRESS ON CHECKOUT
				-------------------------------------------------------------------------
				Title Fullname  : {}
				Company Name    : {}
				Address Line1   : {}
				Address Line2   : {}
				Address Line3   : {}
				Country         : {}
				Phone           : {}
				--------------------------------------------------------------------------
				""", addressline1, addressline2, addressline3, addressline4, addressline5, addressline6, addressline7);

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();

		// Add Invoice summary section
		allureReport.append(String.format("""

				-------------------------------------------------------------------------
				               INVOICE ADDRESS ON CHECKOUT
				-------------------------------------------------------------------------
				Title Fullname  : %s
				Company Name    : %s
				Address Line1   : %s
				Address Line2   : %s
				Address Line3   : %s
				Country         : %s
				Phone           : %s
				-------------------------------------------------------------------------
				""", addressline1, addressline2, addressline3, addressline4, addressline5, addressline6, addressline7));

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Checkout-Invoice Address Details", allureReport.toString());

		captureScreenshot();
		
		return invoiceAddressMap;

	}

	@Step("Enter checkout comment")
	public void enterCheckoutComment(String comment) {
		if (comment.isBlank())
			comment = "The order placed should be delivered as early as possible to the delivery address.";
		checkoutComment.fill(comment);
		captureScreenshot();
	}

	@Step("Click place order button")
	public void clickPlaceOrder() {
		placeOrderButton.click();
		captureScreenshot();
	}

	@Step("Read delivery address displayed on checkout page")
	public void fillFakerPaymentDetails(SignupData data) {
		cardPayeeName.fill(data.getPersonalInfo().getpaymentCardPayeeName());
		cardNumber.fill(data.getPersonalInfo().getpaymentCardNumber());
		cardCvc.fill(data.getPersonalInfo().getpaymentCardCvv());
		cardExpirationMonth.fill(data.getPersonalInfo().getpaymentCardExpirationMonth());
		cardExpirationYear.fill(data.getPersonalInfo().getpaymentCardExpirationYear());
	}

	@Step("Click pay and confirm order")
	public void clickPayAndConfirmOrder() {
		payAndConfirmButton.click();
	}

	@Step("Click download invoice button")
	private void clickDownloadInvoice() {
		downloadInvoiceButton.click();
	}

	private int getCartTableColumnIndex(String columnName) {

		int columnIndex = 0;

		Locator headers = cartTable.locator(".cart_menu td");

		for (Locator header : headers.all()) {
			if (header.textContent().equalsIgnoreCase(columnName)) {
				break;
			} else {
				columnIndex += 1;
				continue;
			}
		}
		return columnIndex;

	}

	@Step("Read all cart product details")
	public Map<String, String> readAllCartProductDetails() {

		Locator rows = cartTable.locator("tbody tr");
		int rowCount = rows.count();
		int rowIndex = 1;

		Map<String, String> productMap = new HashMap<String, String>();

		for (Locator row : rows.all()) {

			Locator cells = row.locator("td");

			if (rowIndex == rowCount) {
				String overallTotalPrice = cells.locator(".cart_total_price").textContent().split(" ")[1];
				productMap.put("Overall Total", overallTotalPrice);
				break;
			}

			String name = cells.nth(getCartTableColumnIndex("Description")).locator("a").textContent().trim();
			String[] description = cells.nth(getCartTableColumnIndex("Description")).locator("p").textContent().trim()
					.split(">");
			String category = description[0].trim();
			String subCategory = description[1].trim();
			String price = cells.nth(getCartTableColumnIndex("Price")).locator("p").textContent().trim().split(" ")[1];
			String quantity = cells.nth(getCartTableColumnIndex("Quantity")).locator(page.getByRole(AriaRole.BUTTON))
					.textContent();
			String total = cells.nth(getCartTableColumnIndex("Total")).locator("p").textContent().split(" ")[1];

			productMap.put("Name" + rowIndex, name);
			productMap.put("Category" + rowIndex, category);
			productMap.put("SubCategory" + rowIndex, subCategory);
			productMap.put("Price" + rowIndex, price);
			productMap.put("Quantity" + rowIndex, quantity);
			productMap.put("Total" + rowIndex, total);

			logger.info("""

									PRODUCT-{} DETAILS FROM CART
					---------------------------------------------------
					Name        : {}
					Category    : {}
					SubCategory : {}
					Price       : {}
					Quantity    : {}
					Total       : {}
					----------------------------------------------------

					""", rowIndex, productMap.get("Name" + rowIndex), productMap.get("Category" + rowIndex),
					productMap.get("SubCategory" + rowIndex), productMap.get("Price" + rowIndex),
					productMap.get("Quantity" + rowIndex), productMap.get("Total" + rowIndex));

			if (rowIndex != rowCount)
				rowIndex += 1;
		}

		logger.info("""

								CART SUMMARY
				---------------------------------------------------
				Total of <{}> Product/s are Added To Cart
				Cart Total Price is '{}'
				----------------------------------------------------

				""", rowCount-1, productMap.get("Overall Total"));

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();

		// Product details section — loop for each product
		for (int i = 1; i < rowCount; i++) {
			allureReport.append(String.format("""
					PRODUCT-%d DETAILS FROM CART
					---------------------------------------------------
					Name        : %s
					Category    : %s
					SubCategory : %s
					Price       : %s
					Quantity    : %s
					Total       : %s
					---------------------------------------------------

					""", i, productMap.get("Name" + i), productMap.get("Category" + i),
					productMap.get("SubCategory" + i), productMap.get("Price" + i), productMap.get("Quantity" + i),
					productMap.get("Total" + i)));
		}

		// Cart summary section
		allureReport.append(String.format("""
				CART SUMMARY
				---------------------------------------------------
				Total of <%d> Product/s are Added To Cart
				Cart Total Price is '%s'
				---------------------------------------------------
				""", rowCount-1, productMap.get("Overall Total")));

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Cart Details", allureReport.toString());

		return productMap;

	}

	@Step("Download invoice and save file")
	public Path downloadInvoice() {

		Download download = page.waitForDownload(() -> {
			clickDownloadInvoice();
		});

		// ✅ Check download didn't fail
		String failure = download.failure();
		Assert.assertNull(failure, "Download Invoice failed: " + failure);

		// ✅ Handle filename
		String fileName = download.suggestedFilename();
		if (fileName == null || fileName.isEmpty()) {
			fileName = "invoice_" + System.currentTimeMillis() + ".txt";
		}

		// ✅ Create downloads directory if not exists
		Path downloadsDir = Paths.get("downloads");
		downloadsDir.toFile().mkdirs();

		Path savePath = Paths.get("downloads/" + fileName);
		download.saveAs(savePath);

		// ✅ Verify file on disk
		File file = savePath.toFile();
		Assert.assertTrue(file.exists(), "Invoice file not found!");
		Assert.assertTrue(file.length() > 0, "Invoice file is empty!");

		logger.info("✅ Invoice downloaded!");
		logger.info("   Name : {}", fileName);
		logger.info("   Size : {} bytes", file.length());
		logger.info("   Path : {}", savePath.toAbsolutePath());

		ReportManager.addFileAttachement(fileName, "text/plain", savePath, ".txt");
		
		ReportManager.addParameter("File Path", savePath);

		return savePath; // ← return path for content verification
	}

	@Step("Verify the invoice content include the custome fullname {firstName} {lastName} and the total amount of purchase as Rs.{totalAmount}")
	public void verifyInvoiceContent(Path invoicePath, String firstName, String lastName, String totalAmount) {
		try {
			// ✅ Read txt file content
			String fileContent = new String(Files.readAllBytes(invoicePath)).replaceAll("\\s+", " ") // normalize spaces
					.trim();

			logger.info("Invoice Content: {}", fileContent);

			// ✅ Build expected message
			String expectedMessage = String.format("Hi %s %s, Your total purchase amount is %s. Thank you", firstName,
					lastName, totalAmount);

			logger.info("Expected Message: {}", expectedMessage);

			// ✅ Verify content
			softAssert.assertTrue(fileContent.contains(expectedMessage), "Invoice content mismatch!\n" + "Expected  : "
					+ expectedMessage + "\n" + "Actual    : " + fileContent);

			logger.info("✅ Invoice content verified!");
		} catch (IOException e) {
			Assert.fail("Failed to read invoice file: " + e.getMessage());
		}
	}

}
