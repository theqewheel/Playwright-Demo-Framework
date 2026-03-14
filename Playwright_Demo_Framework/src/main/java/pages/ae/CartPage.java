package pages.ae;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.databind.introspect.DefaultAccessorNamingStrategy.FirstCharBasedValidator;

public class CartPage extends BasePage {

	private final Locator proceedToCheckOutButton;
	private final Locator cartTable;
	private final Locator deleteItem;
	private final Locator registerLoginLink;

	public CartPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		this.proceedToCheckOutButton = page.getByText("Proceed To Checkout");
		this.cartTable = page.locator("#cart_info_table");
		this.deleteItem = page.locator(".cart_quantity_delete");
		this.registerLoginLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Register / Login"));
	}

	@Step("Verify Subscription Section Header in Cart Page")
	public void verifySubscriptionHeaderCartPage() {
		Assert.assertEquals(super.verifySubscriptionHeaderVisibility(), true);
	}

	@Step("Verify Subscription is success from Cart Page")
	public void verifySubscriptionSuccessfromCartPage() {
		Assert.assertEquals(super.verifySubscriptionSuccess(), true);
	}

	public void clickProceedToCheckout() {
		proceedToCheckOutButton.click();
	}

	public void clickRegisterLoginLink() {
		registerLoginLink.click();
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

	public Map<String, String> readCartProductDetails(String productFullName) {

		Locator matchedRowCells = cartTable.locator("tbody tr").filter(
				new Locator.FilterOptions().setHasText(Pattern.compile(productFullName, Pattern.CASE_INSENSITIVE)))
				.locator("td");

		Locator headerCols = cartTable.locator("thead td");
		int headers = headerCols.count();

		if (matchedRowCells.count() > headers) {
			matchedRowCells = matchedRowCells.first();
			logger.info("Multiple matching products found for - " + productFullName
					+ ", hence choosing the first match found.");
		}

		Map<String, String> productMap = new HashMap<String, String>();

		String name = matchedRowCells.nth(getCartTableColumnIndex("Description")).locator("a").textContent().trim();
		String[] description = matchedRowCells.nth(getCartTableColumnIndex("Description")).locator("p").textContent()
				.trim().split(">");
		String category = description[0].trim();
		String subCategory = description[1].trim();
		String price = matchedRowCells.nth(getCartTableColumnIndex("Price")).locator("p").textContent().trim()
				.split(" ")[1];
		String quantity = matchedRowCells.nth(getCartTableColumnIndex("Quantity"))
				.locator(page.getByRole(AriaRole.BUTTON)).textContent();
		String total = matchedRowCells.nth(getCartTableColumnIndex("Total")).locator("p").textContent().split(" ")[1];

		productMap.put("Name", name);
		productMap.put("Category", category);
		productMap.put("SubCategory", subCategory);
		productMap.put("Price", price);
		productMap.put("Quantity", quantity);
		productMap.put("Total", total);

		return productMap;
	}

	public Map<String, String> readCartProductDetails(int productIndex) {

		Locator matchedRowCells = cartTable.locator("tbody tr").nth(productIndex - 1).locator("td");

		Map<String, String> productMap = new HashMap<String, String>();

		String name = matchedRowCells.nth(getCartTableColumnIndex("Description")).locator("a").textContent().trim();
		String[] description = matchedRowCells.nth(getCartTableColumnIndex("Description")).locator("p").textContent()
				.trim().split(">");
		String category = description[0].trim();
		String subCategory = description[1].trim();
		String price = matchedRowCells.nth(getCartTableColumnIndex("Price")).locator("p").textContent().trim()
				.split(" ")[1];
		String quantity = matchedRowCells.nth(getCartTableColumnIndex("Quantity"))
				.locator(page.getByRole(AriaRole.BUTTON)).textContent();
		String total = matchedRowCells.nth(getCartTableColumnIndex("Total")).locator("p").textContent().split(" ")[1];

		productMap.put("Name", name);
		productMap.put("Category", category);
		productMap.put("SubCategory", subCategory);
		productMap.put("Price", price);
		productMap.put("Quantity", quantity);
		productMap.put("Total", total);

		return productMap;
	}

	public Map<String, String> readAllCartProductDetails() {

		Locator rows = cartTable.locator("tbody tr");
		int rowCount = rows.count();
		int rowIndex = 1;

		Map<String, String> productMap = new HashMap<String, String>();

		for (Locator row : rows.all()) {
			
			Locator cells = row.locator("td");
			
			if(rowIndex==rowCount) {
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

				""", rowCount, productMap.get("Overall Total"));

		return productMap;

	}

	@Step("Verify removal of product using name")
	public void removeProductFromCart(String productName) {

		int totalProductsThen = cartTable.locator("tbody tr").count();

		Locator headerCols = cartTable.locator("thead td");
		int headers = headerCols.count();

		Locator matchedRowCells = cartTable.locator("tbody tr")
				.filter(new Locator.FilterOptions().setHasText(Pattern.compile(productName, Pattern.CASE_INSENSITIVE)))
				.locator("td");

		if (matchedRowCells.count() > headers) {
			matchedRowCells = matchedRowCells.first();
			logger.info("Multiple matching products found for - " + productName
					+ ", hence choosing the first match found.");
		}

		matchedRowCells.locator(deleteItem).click();

		int totalProductsNow = cartTable.locator("tbody tr").count();

		Assert.assertEquals(totalProductsNow, totalProductsThen - 1, "Product - " + productName + " deleted from Cart");
	}

	@Step("Verify removal of product using index")
	public void removeProductFromCart(int productIndex) {

		int totalProductsThen = cartTable.locator("tbody tr").count();

		Locator matchedRowCells = cartTable.locator("tbody tr").nth(productIndex).locator("td");

		matchedRowCells.locator(deleteItem).click();

		int totalProductsNow = cartTable.locator("tbody tr").count();

		Assert.assertEquals(totalProductsNow, totalProductsThen - 1,
				"Product earlier at Index - " + productIndex + " deleted from Cart");
	}

	@Step("Verify removal of all products from cart")
	public void removeAllProductsFromCart() {

		int totalProductsThen = cartTable.locator("tbody tr").count();
		logger.info("Total products in cart before deletion - " + totalProductsThen);

		while (cartTable.locator("tbody tr").count() > 0) {
			cartTable.locator("tbody tr") // always get the first row
					.first().locator(deleteItem).click();

			// page.waitForTimeout(500); // wait for row removal before next iteration
		}

		int totalProductsNow = cartTable.locator("tbody tr").count();
		logger.info("Total products in cart after deletion - " + totalProductsNow);

		Assert.assertEquals(totalProductsNow, 0, "All " + totalProductsThen + " products from cart was deleted !!!");
	}

	public boolean verifyProductExistanceInCart(String productFullName) {
		Locator matchedRowCells = cartTable.filter(
				new Locator.FilterOptions().setHasText(Pattern.compile(productFullName, Pattern.CASE_INSENSITIVE)));

		if (matchedRowCells.count() < 1) {
			logger.info("""

					---------------------------------------------------
					Product {} is Not Visible on Cart
					----------------------------------------------------

					""", productFullName);
			return false;
		}

		logger.info("""

				---------------------------------------------------
				Product {} is Visible on Cart
				----------------------------------------------------

				""", productFullName);

		return true;
	}

}
