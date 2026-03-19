package pages.ae;

import static org.testng.Assert.assertEquals;

import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Page.GetByRoleOptions;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import io.qameta.allure.Step;
import reporting.ReportManager;

public class HomePage extends BasePage {
	
	private final Locator SignupLoginLink;
	private final Locator DeleteAccountLink;
	private final Locator LogOutLink;
	private final Locator NavigationMenuLinks;
	
	public HomePage(Page page, SoftAssert softAssert) {
		super(page,softAssert);
		this.SignupLoginLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Signup / Login"));
		this.DeleteAccountLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Delete Account",Pattern.CASE_INSENSITIVE)));
		this.LogOutLink = page.getByRole(AriaRole.LINK, new GetByRoleOptions().setName("Logout"));
		this.NavigationMenuLinks = page.locator("ul[class*='navbar'] a");
	}

	@Step("Click Signup / Login link")
	public void clickSignupLoginLink() {
		SignupLoginLink.click();
		captureScreenshot();
	}
	
	@Step("Click delete account link")
	public void clickDeleteAccount() {
		DeleteAccountLink.click();
		captureScreenshot();
	}
	
	@Step("Click logout link")
	public void clickLogOut() {
		LogOutLink.click();
		captureScreenshot();
	}
	
	@Step("Get the logged in username")
	public String getLoggedInUsername() {	
		String loggedinMessage = page.getByText("Logged in as").textContent().trim();
		String username = loggedinMessage.substring(loggedinMessage.lastIndexOf(" ") + 1);
		ReportManager.addParameter("Logged in User", username);
		return username;
	}
	
	@Step("Verify Invalid Email Error on Login")
	public void verifyInvalidLoginError() {
		captureScreenshot();
		String errorMessage = page.getByText("Your email or password is incorrect").textContent().trim();
		assertEquals(errorMessage, "Your email or password is incorrect!", "Incorrect Login credentials");
	}
	
	@Step("Click the menu - '{menu}' on the AE Home page")
	public void clickMenu(String menu) {
		NavigationMenuLinks.filter(new Locator.FilterOptions().setHasText(menu)).click();
		logger.info("Navigating to " + menu);					   
	}
	
	@Step("Verify Subscription Section Header in Home Page")
	public void verifySubscriptionHeaderHomePage() {
		captureScreenshot();
		Assert.assertEquals(super.verifySubscriptionHeaderVisibility(), true); 
	}
	
	@Step("Verify Subscription is success from Home Page")
	public void verifySubscriptionSuccessfromHomePage() {
		captureScreenshot();
		Assert.assertEquals(super.verifySubscriptionSuccess(), true); 
	}

}
