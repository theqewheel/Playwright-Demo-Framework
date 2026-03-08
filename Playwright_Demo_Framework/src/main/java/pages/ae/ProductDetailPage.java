package pages.ae;

import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

import framework.base.BasePage;

public class ProductDetailPage extends BasePage {

	private final Locator productInfo;

	public ProductDetailPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		this.productInfo = page.locator(".product-information");
	}

	public void verifyProductDetailsAreVisible() {
		softAssert.assertTrue(fetchProductName() != null, "Product Name is not available");
		softAssert.assertTrue(fetchProductCategory() != null, "Product Category is not available");
		softAssert.assertTrue(fetchProductPrice() != null, "Product Price is not available");
		softAssert.assertTrue(fetchProductAvailability() != null, "Product Availability is not available");
		softAssert.assertTrue(fetchProductCondition() != null, "Product Condition is not available");
		softAssert.assertTrue(fetchProductBrand() != null, "Product Brand is not available");
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

}
