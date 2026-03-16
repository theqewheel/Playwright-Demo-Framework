package pages.ae;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.helpers.Reporter;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import framework.utils.StringUtil;
import io.qameta.allure.Step;
import reporting.ReportManager;

public class ProductsPage extends BasePage {

	private final Locator ProductCardsDiv;
	private final Locator searchProductsTextBox;
	private final Locator searchProductsButton;
	private final Locator categoryLinks;
	private final Locator subCategoryWomenLinks;
	private final Locator subCategoryMenLinks;
	private final Locator subCategoryKidsLinks;
	private final Locator brandNameLinks;
	private final Locator featuredProductTitle;
	private final Locator recommendedSectionHeader;
	private final Locator recommendedProducts;

	public ProductsPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		this.ProductCardsDiv = page.locator(".product-image-wrapper");
		this.searchProductsTextBox = page.getByPlaceholder("Search Product");
		this.searchProductsButton = page.locator("#submit_search");
		this.categoryLinks = page.locator("div[class*='category'] .panel-title");
		this.subCategoryWomenLinks = page.locator("#Women");
		this.subCategoryMenLinks = page.locator("#Men");
		this.subCategoryKidsLinks = page.locator("#Kids");
		this.brandNameLinks = page.locator(".brands-name");
		this.featuredProductTitle = page.locator(".features_items .title");
		this.recommendedSectionHeader = page.locator(".recommended_items .title");
		this.recommendedProducts = page.locator(".recommended_items");
	}

	@Step("Verify products list on load - default")
	public void verifyProductsListonLoad() {
		if (ProductCardsDiv.count() < 1) {
			Assert.fail("No products displayed on ALL Products page - default load");
		}
	}

	@Step("Search for a product")
	public void searchProduct(String productName) {
		searchProductsTextBox.fill(productName);
		searchProductsButton.click();
		page.waitForLoadState();
		try {
			assertThat(page.getByText("Searched Products")).isVisible();
		} catch (Exception e) {
			softAssert.fail("Page Header - Searched Products is not visible");
		}

	}

	@Step("Click category-{category}")
	public void clickCategory(String category) {
		categoryLinks.locator(page.locator("a[href='#" + StringUtil.capitalizeFirst(category) + "']")).click();
	}

	@Step("Click subCategory-{subCategory} under category-{category}")
	public void clickSubCategory(String category, String subCategory) {

		clickCategory(category);

		switch (category.toLowerCase()) {
		case "women":
			subCategoryWomenLinks.locator("a:has-text('" + StringUtil.capitalizeFirst(subCategory) + "')").click();
			break;
		case "men":
			subCategoryMenLinks.locator("a:has-text('" + StringUtil.capitalizeFirst(subCategory) + "')").click();
			break;
		case "kids":
			subCategoryKidsLinks.locator("a:has-text('" + StringUtil.capitalizeFirst(subCategory) + "')").click();
			break;
		default:
			logger.error("Invaid category - " + category + "for the provided subcategory - " + subCategory);
		}
	}

	@Step("Verify category page displayed")
	public void verifyCategoryPageDisplay(String Category, String subCategory) {

		verifyPageLoaded("category_products", subCategory);

		// verify the navigation title for featured products
		softAssert.assertTrue(getCategoryNavigationDisplayed().equalsIgnoreCase(Category + " > " + subCategory));
		logger.error("The category navigation displayed is not >> " + Category + " > " + subCategory + ", Actual: "
				+ getCategoryNavigationDisplayed());

		// verify the featured title
		try {
			assertThat(featuredProductTitle).containsText(
					Pattern.compile(Category + " - " + subCategory + " " + "Products", Pattern.CASE_INSENSITIVE));

		} catch (Exception e) {
			softAssert.fail("The featured title displayed is not >> " + Category + " - " + subCategory + " "
					+ "Products" + ", Actual: " + featuredProductTitle.textContent().trim());
			logger.error("The featured title displayed is not >> " + Category + " - " + subCategory + " " + "Products"
					+ ", Actual: " + featuredProductTitle.textContent().trim());
		}

	}

	private Locator getProductCard(String productName) {
		return ProductCardsDiv.filter(new Locator.FilterOptions().setHasText(productName));
	}

	private Locator getProductCard(int productIndex) {
		return ProductCardsDiv.nth(productIndex);
	}

	private Locator getAllProductCards() {
		return ProductCardsDiv;
	}

	@Step("Click view product button on the product '{productName}'")
	public void clickViewProduct(String productName) {
		Locator productCard = getProductCard(productName);
		if (productCard.count() > 1) {
			logger.warn("Multiple matching products found for - " + productName
					+ ", hence choosing the first match found.");
			productCard = productCard.first();
		}
		productCard.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Product")).click();
	}

	@Step("Click view product with index '{productIndex}'")
	public void clickViewProduct(int productIndex) {
		Locator productCard = getProductCard(productIndex - 1);
		productCard.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Product")).click();
	}

	@Step("Click add to cart for the product '{productName}'")
	public void clickAddProductToCart(String productName) {
		Locator productCard = getProductCard(productName);
		if (productCard.count() > 1) {
			logger.warn("Multiple matching products found for - " + productName
					+ ", hence choosing the first match found.");
			productCard = productCard.first();
		}
		productCard.hover();
		productCard.locator(".product-overlay .add-to-cart").click();
	}

	@Step("Click add to cart for the product with index '{productIndex}'")
	public void clickAddProductToCart(int productIndex) {
		Locator productCard = getProductCard(productIndex - 1);
		productCard.hover();
		productCard.locator(".product-overlay .add-to-cart").click();
	}

	@Step("Verify success message on clicking - Add to cart")
	public void verifyAddtoCartSuccessMessage() {
		try {
			assertThat(page.getByText("Your product has been added to cart.")).isVisible();
		} catch (Exception e) {
			softAssert.fail("Success Message on Adding to cart is not visible");
		}
	}

	@Step("Click continue shopping button")
	public void clickContinueShopping() {
		page.getByText("Continue Shopping").click();
	}

	@Step("Click view cart link")
	public void clickViewCart() {
		page.getByText("View Cart").click();
	}

	@Step("Verify Subscription is success from Products Page")
	public void verifySubscriptionSuccessfromProductsPage() {
		Assert.assertEquals(super.verifySubscriptionSuccess(), true);
	}

	@Step("Verify Searched Products for the search keyword '{productName}'")
	public void verifySearchedProducts(String productName) {

		int productCount = getProductCard(productName).count();

		Assert.assertTrue(productCount > 0,
				"The searched product - " + productName + " has not found any matching products.");

		if (productCount > 0) {
			logger.info("The searched product - " + productName + " has found " + productCount + " matching products.");

			List<String> productNames = getProductCard(productName).locator(".productinfo").locator("p")
					.allTextContents();

			logger.info("""

					   Search List of Products
					------------------------------
					{}
					------------------------------
					""", String.join(", ", productNames));

			ReportManager.logStep("The search list displayed products are - " + String.join(", ", productNames));

			// ✅ Build the same formatted string for Allure
			StringBuilder allureReport = new StringBuilder();

			// Product details section
			allureReport.append(String.format("""

					   Search List of Products
					------------------------------
					%s
					------------------------------
					""", String.join(", ", productNames)));

			// ✅ Attach to Allure — shows as collapsible section in report
			ReportManager.attachTextContentAsSection("Products List on Search Keyword - " + productName,
					allureReport.toString());

		} else {
			logger.warn("The searched product - " + productName + " has not found any matching products.");
			Assert.assertTrue(productCount == 0,
					"The searched product - " + productName + " has not found any matching products.");
		}
	}

	@Step("Verify Searched Products for the keyword '{productName}' has expected count of products as '{listCount}'")
	public void verifySearchedProducts(String productName, int listCount) {

		int productCountActual = getProductCard(productName).count();

		if (productCountActual == listCount) {
			logger.info("The searched product - " + productName + " has found " + listCount
					+ " matching products as expected.");

			List<String> productNames = getProductCard(productName).allTextContents();

			logger.info("""

					   Search List of Products
					------------------------------
					{}
					------------------------------
					""", String.join(", ", productNames));

			ReportManager.logStep("The search list displayed products are - " + String.join(", ", productNames));

			// ✅ Build the same formatted string for Allure
			StringBuilder allureReport = new StringBuilder();

			// Product details section
			allureReport.append(String.format("""

					   Search List of Products
					------------------------------
					%s
					------------------------------
					""", String.join(", ", productNames)));

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Products List on Search Keyword - " + productName,
				allureReport.toString());

	}else

	{
		logger.warn("The searched product - " + productName + " has not found " + listCount
				+ " matching products. Expected count: " + listCount);
		Assert.assertTrue(productCountActual == listCount, "The searched product - " + productName + " has not found "
				+ listCount + " matching products. Expected count: " + listCount);
	}

	}

	@Step("Read product details for the product name - '{productName}'")
	private Map<String, String> readProductDetails(String productName) {

		Map<String, String> productDetailMap = new HashMap<String, String>();

		String productFullName = getProductCard(productName).locator(".productinfo p").first().textContent().trim();

		String productPrice = getProductCard(productName).locator(".productinfo h2").first().textContent().trim()
				.split("Rs. ")[1];

		if (productFullName != null)
			productDetailMap.put("Name", productFullName);
		else {
			logger.error("The Product Name is missing");
		}

		if (productPrice != null)
			productDetailMap.put("Price", productPrice);
		else {
			logger.error("The Product Price is missing");
		}

		ReportManager.attachTextContentAsSection("Product Details",
				StringUtil.formattedStringForMaps(productDetailMap).toString());

		return productDetailMap;

	}

	@Step("Read product details for the product index - '{productIndex}'")
	private Map<String, String> readProductDetails(int productIndex) {

		Map<String, String> productDetailMap = new HashMap<String, String>();

		String productFullName = getProductCard(productIndex - 1).locator(".productinfo p").textContent().trim();

		String productPrice = getProductCard(productIndex - 1).locator(".productinfo h2").textContent().trim()
				.split("Rs. ")[1];

		if (productPrice != null)
			productDetailMap.put("Price" + productIndex, productPrice);
		else {
			logger.error("The Product Price is missing");
		}
		if (productFullName != null)
			productDetailMap.put("Name" + productIndex, productFullName);
		else {
			logger.error("The Product Name is missing");
		}

		ReportManager.attachTextContentAsSection("Product Details",
				StringUtil.formattedStringForMaps(productDetailMap).toString());

		return productDetailMap;

	}

	@Step("Read product price for the product name - '{productName}'")
	public String readProductPrice(String productName) {
		String productPrice = readProductDetails(productName).get("Price");
		ReportManager.addParameter("Product Price", productPrice);
		return productPrice;
	}

	@Step("Read product full name for the product name - '{productName}'")
	public String readProductFullName(String productName) {
		String productFullName = readProductDetails(productName).get("Name");
		ReportManager.addParameter("Product Name", productName);
		return productFullName;
	}

	@Step("Read product price for the product index - '{productIndex}'")
	public String readProductPrice(int productIndex) {
		String productPrice = readProductDetails(productIndex).get("Price" + productIndex);
		ReportManager.addParameter("Product Price", productPrice);
		return productPrice;
	}

	@Step("Read product full name for the product index - '{productIndex}'")
	public String readProductFullName(int productIndex) {
		String productFullName = readProductDetails(productIndex).get("Name" + productIndex);
		ReportManager.addParameter("Product Name", productFullName);
		return productFullName;
	}

	@Step("Select the brand '{brandName}'")
	public void selectBrand(String brandName) {
		brandNameLinks.locator("a[href*='" + StringUtil.capitalizeFirst(brandName) + "']").click();
	}

	@Step("Verify brand page displayed for the brand - '{brandName}'")
	public void verifyBrandPageDisplay(String brandName) {

		verifyPageLoaded("brand_products", brandName);

		// verify the navigation title for featured products
		softAssert.assertTrue(getCategoryNavigationDisplayed().equalsIgnoreCase(brandName));
		logger.error("The category navigation displayed is not >> " + brandName + ", Actual: "
				+ getCategoryNavigationDisplayed());

		// verify the featured title
		try {
			assertThat(featuredProductTitle)
					.containsText(Pattern.compile("Brand - " + brandName + " Products", Pattern.CASE_INSENSITIVE));

		} catch (Exception e) {
			softAssert.fail("The featured title displayed is not >> " + "Brand - " + brandName + " Products"
					+ ", Actual: " + featuredProductTitle.textContent().trim());
			logger.error("The featured title displayed is not >> " + "Brand - " + brandName + " Products" + ", Actual: "
					+ featuredProductTitle.textContent().trim());
		}

	}

	@Step("Add multiple products '<<count - {productCount}>>' to cart and retrieve product details ")
	public Map<String, String> addMultipleProductsToCartAndRetreiveProductDetails(int productCount) {

		Map<String, String> ProductDetails = new HashMap<String, String>();

		if (productCount <= 0) {
			Assert.fail("The product count should be atleast '1' to proceed with Add to Cart.");
			return null;
		}

		for (int i = 1; i <= productCount; i++) {
			ProductDetails.put("Name" + i, readProductFullName(i));
			ProductDetails.put("Price" + i, readProductPrice(i));
			clickAddProductToCart(i);
			if (i == productCount)
				clickViewCart(); // view the cart once last product is added
			else
				clickContinueShopping();
			logger.info("""

					---------------------------------------------------
					Product {} Added To Cart
					Name  : {}
					Price : {}
					------------------------xxxx-----------------------
					""", i, ProductDetails.get("Name" + i), ProductDetails.get("Price" + i));
		}

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();

		// Product details section — loop for each product
		for (int i = 1; i <= productCount; i++) {
			allureReport.append(String.format("""

					---------------------------------------------------
					Product %s Added To Cart
					Name  : %s
					Price : %s
					------------------------xxxx-----------------------
					""", i, ProductDetails.get("Name" + i), ProductDetails.get("Price" + i)));
		}

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Added To Cart - Product Details", allureReport.toString());

		return ProductDetails;
	}

	@Step("Add multiple products '<<productNames>>' to cart and retrieve product details ")
	public Map<String, String> addMultipleProductsToCartAndRetreiveProductDetails(List<String> productNames) {

		Map<String, String> ProductDetails = new HashMap<String, String>();

		if (productNames.size() <= 0) {
			Assert.fail("The product count should be atleast '1' to proceed with Add to Cart.");
			return null;
		}

		for (int i = 1; i <= productNames.size(); i++) {
			ProductDetails.put("Name" + i, readProductFullName(productNames.get(i - 1)));
			ProductDetails.put("Price" + i, readProductPrice(productNames.get(i - 1)));
			clickAddProductToCart(productNames.get(i - 1));
			if (i == productNames.size())
				clickViewCart(); // view the cart once last product is added
			else
				clickContinueShopping();
			logger.info("""

					---------------------------------------------------
					Product {} Added To Cart
					Name  : {}
					Price : {}
					------------------------xxxx-----------------------
					""", i, ProductDetails.get("Name" + i), ProductDetails.get("Price" + i));
		}

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();

		// Product details section — loop for each product
		for (int i = 1; i <= productNames.size(); i++) {
			allureReport.append(String.format("""

					---------------------------------------------------
					Product %s Added To Cart
					Name  : %s
					Price : %s
					------------------------xxxx-----------------------
					""", i, ProductDetails.get("Name" + i), ProductDetails.get("Price" + i)));
		}

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Added To Cart - Product Details", allureReport.toString());

		return ProductDetails;
	}

	@Step("Add multiple products to Cart <<product count: {productCount}>>")
	public void addMultipleProductsToCart(int productCount) {

		if (productCount <= 0) {
			Assert.fail("The product count should be atleast '1' to proceed with Add to Cart.");
		}

		for (int i = 1; i <= productCount; i++) {
			clickAddProductToCart(i);
			clickContinueShopping();
		}
	}

	@Step("Add multiple products to Cart <<product names: {productNames}>>")
	public void addMultipleProductsToCart(List<String> productNames) {

		if (productNames.size() <= 0) {
			Assert.fail("The product count should be atleast '1' to proceed with Add to Cart.");
		}

		for (int i = 1; i < productNames.size(); i++) {
			clickAddProductToCart(productNames.get(i));
			clickContinueShopping();
		}
	}

	@Step("Read product name at index '{productIndex}'")
	private String getProductName(int productIndex) {
		String productName = ProductCardsDiv.nth(productIndex).locator(".productinfo p").textContent().trim();
		ReportManager.addParameter("Product Name", productName);
		return productName;
	}

	@Step("Read product price at index '{productIndex}'")
	private String getProductPrice(int productIndex) {
		String productPrice = ProductCardsDiv.nth(productIndex).locator(".productinfo h2").textContent().trim()
				.split("Rs. ")[1];
		ReportManager.addParameter("Product Price", productPrice);
		return productPrice;
	}

	@Step("Add all products visible on the 'Products' page to the shopping cart")
	public List<String> addAllProductsVisibleToCart() {

		List<String> productList = new ArrayList<String>();
		int totalProductsVisible = getAllProductCards().count();
		logger.info("Total Products displayed on screen - " + totalProductsVisible);
		int totalProductsAddedToCart = 0;

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();
				
		for (int i = 0; i < totalProductsVisible; i++) {
			productList.add(getProductName(i));
			clickAddProductToCart(i);
			totalProductsAddedToCart++;
			logger.info("""

					---------------------------------------------------
					Product {}: {} Added To Cart with Price {}
					----------------------------------------------------

					""", i, getProductName(i), getProductPrice(i));
			
			// Product details section — loop for each product
			allureReport.append(String.format("""

					---------------------------------------------------
					Product %d: %s Added To Cart with Price %s
					----------------------------------------------------

					""", i, getProductName(i), getProductPrice(i)));
			
			if (i == totalProductsVisible - 1)
				clickViewCart(); // view the cart once last product is added
			else
				clickContinueShopping();
		}

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Added To Cart - Product Details", allureReport.toString());

		logger.info("Total Products added to cart - " + totalProductsAddedToCart);
		return productList;
	}

	@Step("Verify the recommended product section")
	public void verifyRecommendedSectionDisplayed() {

		recommendedSectionHeader.scrollIntoViewIfNeeded();
		Assert.assertTrue(recommendedSectionHeader.isVisible(), "The recommended items section is not visible");
		Assert.assertEquals(recommendedSectionHeader.textContent().trim().toUpperCase(),
				"Recommended Items".toUpperCase());

		Locator recommendedProduct = recommendedProducts.locator(".item.active .product-image-wrapper");
		if (recommendedProduct.count() > 0) {
			logger.info("Recommended Items list shows " + recommendedProduct.count() + " items.");
		} else {
			Assert.fail("The Recommended Items list is Empty !!!");
		}
	}

	private Locator getActiveRecommendedProducts(String productName) {
		return recommendedProducts.locator(".item.active .product-image-wrapper")
				.filter(new Locator.FilterOptions().setHasText(productName));
	}

	@Step("Add a recommended product to cart for product name - '{productName}'")
	public void addRecommendedProductToCart(String productName) {

		int maxAttempts = 5; // max time to rotate carousel
		int attempts = 0;

		// check if the product exists in carousel
		// wait for it to become visible
		while (getActiveRecommendedProducts(productName).count() < 1 && attempts < maxAttempts) {
			logger.info("Product '{}' is not active yet, clicking next attempt{}/{}...", productName, attempts + 1,
					maxAttempts);
			page.locator(".right.recommended-item-control").click();
			page.waitForTimeout(500);
			attempts++;
		}

		if (attempts == maxAttempts) {
			Assert.fail(
					"The product '" + productName + "' never became active on the page recommended products section");
		}

		// when product is active on carousel
		getActiveRecommendedProducts(productName).locator(".add-to-cart").click();
		logger.info("Added recommended Product '{}' to cart.", productName);
	}

	@Step("Add any active recommended product to the shopping cart")
	public String addActiveRecommendedProductToCart() {

		Locator recommendedProduct = recommendedProducts.locator(".item.active .product-image-wrapper").first();
		String productName = recommendedProduct.locator("p").textContent().trim();
		recommendedProduct.locator("a").click();
		logger.info("Added recommended Product '{}' to cart.", productName);
		clickViewCart();
		ReportManager.addParameter("Product Name", productName);
		return productName;
	}

}
