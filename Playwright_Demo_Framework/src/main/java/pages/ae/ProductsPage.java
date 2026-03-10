package pages.ae;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.swing.plaf.synth.SynthIcon;

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

	public void clickCategory(String category) {
		categoryLinks.locator(page.locator("a[href='#" + StringUtil.capitalizeFirst(category) + "']")).click();
	}

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

	public void clickViewProduct(String productName) {
		Locator productCard = getProductCard(productName);
		if (productCard.count() > 1) {
			logger.warn("Multiple matching products found for - " + productName
					+ ", hence choosing the first match found.");
			productCard = productCard.first();
		}
		productCard.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Product")).click();
	}

	public void clickViewProduct(int productIndex) {
		Locator productCard = getProductCard(productIndex - 1);
		productCard.getByRole(AriaRole.LINK, new Locator.GetByRoleOptions().setName("View Product")).click();
	}

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

	public void clickContinueShopping() {
		page.getByText("Continue Shopping").click();
	}

	public void clickViewCart() {
		page.getByText("View Cart").click();
	}

	@Step("Verify Subscription is success from Products Page")
	public void verifySubscriptionSuccessfromProductsPage() {
		Assert.assertEquals(super.verifySubscriptionSuccess(), true);
	}

	@Step("Verify Searched Products")
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

		} else {
			logger.warn("The searched product - " + productName + " has not found any matching products.");
			Assert.assertTrue(productCount == 0,
					"The searched product - " + productName + " has not found any matching products.");
		}
	}

	@Step("Verify Searched Products has expected count of products")
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

		} else {
			logger.warn("The searched product - " + productName + " has not found " + listCount
					+ " matching products. Expected count: " + listCount);
			Assert.assertTrue(productCountActual == listCount, "The searched product - " + productName
					+ " has not found " + listCount + " matching products. Expected count: " + listCount);
		}

	}

	private Map<String, String> readProductDetails(String productName) {

		Map<String, String> productDetailMap = new HashMap<String, String>();

		String productPrice = getProductCard(productName)
				.filter(new Locator.FilterOptions().setHas(page.locator("div[class*='productinfo'")))
				.filter(new Locator.FilterOptions().setHas(page.locator("h2"))).textContent().trim();

		productPrice = productPrice.substring(productPrice.lastIndexOf("Rs. "));

		String productFullName = getProductCard(productName)
				.filter(new Locator.FilterOptions().setHas(page.locator("div[class*='productinfo'")))
				.filter(new Locator.FilterOptions().setHas(page.locator("p"))).textContent();

		if (productPrice != null)
			productDetailMap.put("Price", productPrice);
		else {
			logger.error("The Product Price is missing");
		}
		if (productFullName != null)
			productDetailMap.put("Name", productFullName);
		else {
			logger.error("The Product Name is missing");
		}

		return productDetailMap;

	}

	public String readProductPrice(String productName) {
		String productPrice = readProductDetails(productName).get("Price");
		return productPrice;
	}

	public String readProductFullName(String productName) {
		String productFullName = readProductDetails(productName).get("Name");
		return productFullName;
	}

	public void selectBrand(String brandName) {
		brandNameLinks.locator("a[href*='" + StringUtil.capitalizeFirst(brandName) + "']").click();
	}

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

}
