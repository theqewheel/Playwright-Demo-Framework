package pages.ae;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Page;

import framework.base.BasePage;
import io.qameta.allure.Step;

public class CartPage extends BasePage {

	public CartPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		// TODO Auto-generated constructor stub
	}

	@Step("Verify Subscription Section Header in Cart Page")
	public void verifySubscriptionHeaderCartPage() {
		Assert.assertEquals(super.verifySubscriptionHeaderVisibility(), true); 
	}
	
	@Step("Verify Subscription is success from Cart Page")
	public void verifySubscriptionSuccessfromCartPage() {
		Assert.assertEquals(super.verifySubscriptionSuccess(), true); 
	}
}
