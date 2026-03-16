package pages.ae;

import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import io.qameta.allure.Step;
import reporting.ReportManager;

public class ProductDetailPage extends BasePage {

	private final Locator productInfo;
	private final Locator reviewCardName;
	private final Locator reviewCardEmail;
	private final Locator reviewCardReviewBody;
	private final Locator reviewSubmitButton;
	private final Locator quantityTextBox;
	private final Locator addToCartButton;
	private final Locator viewCartLink;
	private final Locator continueShoppingButton;

	public ProductDetailPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		this.productInfo = page.locator(".product-information");
		this.reviewCardName = page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Your Name"));
		this.reviewCardEmail = page.getByRole(AriaRole.TEXTBOX,
				new Page.GetByRoleOptions().setName("Email Address").setExact(true));
		;
		this.reviewCardReviewBody = page.getByRole(AriaRole.TEXTBOX,
				new Page.GetByRoleOptions().setName("Add Review Here"));
		this.reviewSubmitButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Submit"));
		this.quantityTextBox = page.locator("#quantity");
		this.addToCartButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add to cart"));
		this.viewCartLink = page.getByText("View Cart");
		this.continueShoppingButton = page.getByRole(AriaRole.BUTTON,
				new Page.GetByRoleOptions().setName("Continue Shopping"));
	}

	@Step("Verify the product details are visible on the product detail page")
	public void verifyProductDetailsAreVisible() {
		softAssert.assertTrue(fetchProductName() != null, "Product Name is not available");
		softAssert.assertTrue(fetchProductCategory() != null, "Product Category is not available");
		softAssert.assertTrue(fetchProductPrice() != null, "Product Price is not available");
		softAssert.assertTrue(fetchProductAvailability() != null, "Product Availability is not available");
		softAssert.assertTrue(fetchProductCondition() != null, "Product Condition is not available");
		softAssert.assertTrue(fetchProductBrand() != null, "Product Brand is not available");
		logger.info("""

				Product Details
				---------------
				Name     	 :{}
				Category 	 :{}
				Price    	 :{}
				Availability :{}
				Condition    :{}
				Brand        :{}
				""", fetchProductName(), fetchProductCategory(), fetchProductPrice(), fetchProductAvailability(),
				fetchProductCondition(), fetchProductBrand());

		// ✅ Build the same formatted string for Allure
		StringBuilder allureReport = new StringBuilder();

		// Add product detail section
		allureReport.append(String.format("""

				Product Details
				---------------
				Name     	 :%s
				Category 	 :%s
				Price    	 :%s
				Availability :%s
				Condition    :%s
				Brand        :%s
				""", fetchProductName(), fetchProductCategory(), fetchProductPrice(), fetchProductAvailability(),
				fetchProductCondition(), fetchProductBrand()));

		// ✅ Attach to Allure — shows as collapsible section in report
		ReportManager.attachTextContentAsSection("Product Details", allureReport.toString());
	}

	@Step("Fetch the product name")
	public String fetchProductName() {
		String productName = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("h2"))).textContent();
		ReportManager.addParameter("Product Name", productName);
		return productName;
	}

	@Step("Fetch the product category")
	public String fetchProductCategory() {
		String productCategory = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Category"))).textContent().trim();

		productCategory = productCategory.substring(productCategory.lastIndexOf(":")).trim();
		ReportManager.addParameter("Product Category", productCategory);
		
		return productCategory;
	}

	@Step("Fetch product price")
	public String fetchProductPrice() {
		String productPrice = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("span")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Rs."))).textContent().trim();

		productPrice = productPrice.substring(productPrice.lastIndexOf("Rs.")).trim();
		ReportManager.addParameter("Product Price", productPrice);
		return productPrice;
	}

	@Step("Fetch product availability")
	public String fetchProductAvailability() {
		String productAvailability = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Availability"))).textContent().trim();

		productAvailability = productAvailability.substring(productAvailability.lastIndexOf("Availability:")).trim();
		ReportManager.addParameter("Product Availability", productAvailability);
		return productAvailability;
	}

	@Step("Fetch product condition")
	public String fetchProductCondition() {
		String productCondition = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Condition"))).textContent().trim();

		productCondition = productCondition.substring(productCondition.lastIndexOf("Condition:")).trim();
		ReportManager.addParameter("Product Condition", productCondition);
		return productCondition;
	}

	@Step("Fetch product brand")
	public String fetchProductBrand() {
		String productBrand = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Brand"))).textContent().trim();

		productBrand = productBrand.substring(productBrand.lastIndexOf("Brand:")).trim();
		ReportManager.addParameter("Product Brand", productBrand);
		return productBrand;
	}

	@Step("Verify write a review page")
	public void verifyWriteAReviewPage() {
		verifyTextMessageDisplayed("Write Your Review", true);
	}

	@Step("Write product review and submit with name-{name}, email-{email} & content-{}")
	public void writeProductReviewAndSubmit(String name, String email, String content) {
		reviewCardName.fill(name);
		reviewCardEmail.fill(email);
		reviewCardReviewBody.fill(content);
		reviewSubmitButton.click();
	}

	@Step("Verify review submission success message")
	public void verifyReviewSubmissionSuccessMessage() {
		verifyTextMessageDisplayed("Thank you for your review", true);
	}

	@Step("Increase purchase quantity on product as {productQuantity}")
	public void increaseProductPurchaseQuantity(int productQuantity) {
		quantityTextBox.fill(String.valueOf(productQuantity));
	}

	@Step("Click view cart")
	public void clickViewCart() {
		viewCartLink.click();
	}

	@Step("Click add to cart")
	public void addToCart() {
		addToCartButton.click();
	}

	@Step("Click continue shopping")
	public void clickContinueShopping() {
		continueShoppingButton.click();
	}
}
