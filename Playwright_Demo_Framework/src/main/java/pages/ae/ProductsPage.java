package pages.ae;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import io.qameta.allure.Step;
import reporting.ReportManager;

public class ProductsPage extends BasePage {

	private final Locator ProductCardsDiv;
	private final Locator searchProductsTextBox;
	private final Locator searchProductsButton;
	private final Locator categoryLinks;
	private final Locator subscriptionHeader;
	private final Locator subscriptionEmailTextBox;
	private final Locator subscribeButton;
	private final Locator subCategoryWomenLinks;
	private final Locator subCategoryMenLinks;
	private final Locator subCategoryKidsLinks;
	private final Locator brandNameLinks;

	public ProductsPage(Page page,SoftAssert softAssert) {
		super(page,softAssert);
		this.ProductCardsDiv = page.locator(".product-image-wrapper");
		this.searchProductsTextBox = page.getByPlaceholder("Search Product");
		this.searchProductsButton = page.getByRole(AriaRole.BUTTON)
				.filter(new Locator.FilterOptions().setHas(page.locator("#submit_search")));
		this.categoryLinks = page.locator("div[class*='category'] .panel-title a");
		this.subscriptionHeader = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Subscription"));
		this.subscriptionEmailTextBox = page.getByPlaceholder("email address");
		this.subscribeButton = page.getByRole(AriaRole.BUTTON)
				.filter(new Locator.FilterOptions().setHas(page.locator("#subscribe")));
		this.subCategoryWomenLinks = page.locator("#Women")
				.filter(new Locator.FilterOptions().setHas(page.locator(".panel-collapse")))
				.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK)));
		this.subCategoryMenLinks = page.locator("#Men")
				.filter(new Locator.FilterOptions().setHas(page.locator(".panel-collapse")))
				.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK)));
		this.subCategoryKidsLinks = page.locator("#Kids")
				.filter(new Locator.FilterOptions().setHas(page.locator(".panel-collapse")))
				.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK)));
		this.brandNameLinks = page.locator(".brands-name")
				.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK)));
	}

	@Step("Verify products list on load - default")
	public void verifyProductsListonLoad() {
		if(ProductCardsDiv.count()<1) {
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
		}catch(Exception e) {
			softAssert.fail("Page Header - Searched Products is not visible");
		}
		
	}

	public void clickCategory(String category) {
		categoryLinks.filter(new Locator.FilterOptions().setHas(page.getByText(category))).click();
	}

	public void clickSubCategory(String category, String subCategory) {
		switch (category.toLowerCase()) {
		case "women":
			categoryLinks.filter(new Locator.FilterOptions().setHas(page.getByText(category))).click();
			subCategoryWomenLinks.filter(new Locator.FilterOptions().setHas(page.getByText(subCategory))).click();
			break;
		case "men":
			categoryLinks.filter(new Locator.FilterOptions().setHas(page.getByText(category))).click();
			subCategoryMenLinks.filter(new Locator.FilterOptions().setHas(page.getByText(subCategory))).click();
			break;
		case "kids":
			categoryLinks.filter(new Locator.FilterOptions().setHas(page.getByText(category))).click();
			subCategoryKidsLinks.filter(new Locator.FilterOptions().setHas(page.getByText(subCategory))).click();
			break;
		default:
			logger.error("Invaid category - " + category + "for the provided subcategory - " + subCategory);
		}
	}

	public void clickViewProduct(String productName) {
		ProductCardsDiv
		.filter(new Locator.FilterOptions().setHas(page.getByText(productName)))
		.filter(new Locator.FilterOptions().setHas(page.locator(".choose")))
		.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View Product"))))
		.click();
	}
	
	public void clickViewProduct(int productIndex) {
		ProductCardsDiv
		.filter(new Locator.FilterOptions().setHas(page.locator(".choose")))
		.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("View Product"))))
		.nth(productIndex)
		.click();
	}
	
	public void clickAddProductToCart(String productName) {
		ProductCardsDiv
		.filter(new Locator.FilterOptions().setHas(page.getByText(productName)))
		.filter(new Locator.FilterOptions().setHas(page.locator(".product-overlay")))
		.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add to cart"))))
		.click();
	}
	
	public void clickAddProductToCart(int productIndex) {
		ProductCardsDiv
		.filter(new Locator.FilterOptions().setHas(page.locator(".product-overlay")))
		.filter(new Locator.FilterOptions().setHas(page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Add to cart"))))
		.nth(productIndex)
		.click();
	}
	
	@Step("Verify success message on clicking - Add to cart")
	public void verifyAddtoCartSuccessMessage() {
		try {
			assertThat(page.getByText("Your product has been added to cart.")).isVisible();
		}catch(Exception e) {
			softAssert.fail("Success Message on Adding to cart is not visible");
		}
	}
	
	public void clickContinueShopping() {
		page.getByText("Continue Shopping").click();
	}
	
	public void clickViewCart() {
		page.getByText("View Cart").click();
	}

	public void subscribe(String email) {
		subscriptionEmailTextBox.fill(email);
		subscribeButton.click();
	}

	@Step("Verify Subscription is success from Products Page")
	public void verifySubscriptionSuccess() {
		assertThat(page.getByText("You have been successfully subscribed!")).isVisible();
	}

	@Step("Verify Searched Products")
	public void verifySearchedProducts(String productName) {

		int productCount = ProductCardsDiv.filter(new Locator.FilterOptions().setHas(page.getByText(productName)))
				.count();

		Assert.assertTrue(productCount > 0,
				"The searched product - " + productName + " has not found any matching products.");

		if (productCount > 0) {
			logger.info("The searched product - " + productName + " has found " + productCount + " matching products.");
		} else {
			logger.warn("The searched product - " + productName + " has found " + productCount + " matching products.");
		}

	}

	@Step("Verify Searched Products has expected count of products")
	public void verifySearchedProducts(String productName, int listCount) {

		int productCountActual = ProductCardsDiv.filter(new Locator.FilterOptions().setHas(page.getByText(productName)))
				.count();

		Assert.assertTrue(productCountActual == listCount,
				"The searched product - " + productName + " has not found " + listCount + " matching products. Expected count: " + listCount);

		if (productCountActual == listCount) {
			logger.info("The searched product - " + productName + " has found " + listCount + " matching products as expected.");
		} else {
			logger.warn("The searched product - " + productName + " has not found " + listCount + " matching products. Expected count: " + listCount);
		}

	}
	
	private Map<String,String> readProductDetails(String productName) {
		
		Map<String, String> productDetailMap = new HashMap<String, String>();
		
		String productPrice = ProductCardsDiv
				.filter(new Locator.FilterOptions().setHas(page.getByText(productName)))
				.filter(new Locator.FilterOptions().setHas(page.locator("div[class*='productinfo'")))
				.filter(new Locator.FilterOptions().setHas(page.locator("h2"))).textContent().trim();
		
		productPrice = productPrice.substring(productPrice.lastIndexOf("Rs. "));
		
		String productFullName = ProductCardsDiv
				.filter(new Locator.FilterOptions().setHas(page.getByText(productName)))
				.filter(new Locator.FilterOptions().setHas(page.locator("div[class*='productinfo'")))
				.filter(new Locator.FilterOptions().setHas(page.locator("p"))).textContent();
		
		if(productPrice!=null) productDetailMap.put("Price", productPrice);
		else {
			logger.error("The Product Price is missing");
		}
		if(productFullName!=null) productDetailMap.put("Name", productFullName);
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
		brandNameLinks.filter(new Locator.FilterOptions().setHas(page.getByText(brandName))).click();
	}

}
