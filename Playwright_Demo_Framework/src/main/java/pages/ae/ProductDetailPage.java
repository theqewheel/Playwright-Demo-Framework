package pages.ae;

import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;

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
		this.continueShoppingButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Continue Shopping"));
	}

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
	}

	public String fetchProductName() {
		String productName = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("h2"))).textContent();
		return productName;
	}

	public String fetchProductCategory() {
		String productCategory = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Category"))).textContent().trim();

		productCategory = productCategory.substring(productCategory.lastIndexOf(":")).trim();

		return productCategory;
	}

	public String fetchProductPrice() {
		String productPrice = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("span")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Rs."))).textContent().trim();

		productPrice = productPrice.substring(productPrice.lastIndexOf("Rs.")).trim();

		return productPrice;
	}

	public String fetchProductAvailability() {
		String productAvailability = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Availability"))).textContent().trim();

		productAvailability = productAvailability.substring(productAvailability.lastIndexOf("Availability:")).trim();

		return productAvailability;
	}

	public String fetchProductCondition() {
		String productCondition = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Condition"))).textContent().trim();

		productCondition = productCondition.substring(productCondition.lastIndexOf("Condition:")).trim();

		return productCondition;
	}

	public String fetchProductBrand() {
		String productBrand = productInfo.filter(new Locator.FilterOptions().setHas(page.locator("p")))
				.filter(new Locator.FilterOptions().setHas(page.getByText("Brand"))).textContent().trim();

		productBrand = productBrand.substring(productBrand.lastIndexOf("Brand:")).trim();

		return productBrand;
	}

	public void verifyWriteAReviewPage() {
		verifyTextMessageDisplayed("Write Your Review", true);
	}

	public void writeProductReviewAndSubmit(String name, String email, String content) {
		reviewCardName.fill(name);
		reviewCardEmail.fill(email);
		reviewCardReviewBody.fill(content);
		reviewSubmitButton.click();
	}

	public void verifyReviewSubmissionSuccessMessage() {
		verifyTextMessageDisplayed("Thank you for your review", true);
	}

	public void increaseProductPurchaseQuantity(int productQuantity) {
		quantityTextBox.fill(String.valueOf(productQuantity));
	}
	
	public void clickViewCart() {
		viewCartLink.click();
	}
	
	public void addToCart() {
		addToCartButton.click();
	}
	
	public void clickContinueShopping() {
		continueShoppingButton.click();
	}
}
