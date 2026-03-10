package pages.ae;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

import java.util.List;
import java.util.regex.Pattern;

import org.testng.Assert;
import org.testng.asserts.SoftAssert;

import com.github.javafaker.Superhero;
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
		this.SignupLoginLink = page.getByText(Pattern.compile("Signup",Pattern.CASE_INSENSITIVE));
		this.DeleteAccountLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Delete Account",Pattern.CASE_INSENSITIVE)));
		this.LogOutLink = page.getByRole(AriaRole.LINK, new GetByRoleOptions().setName("Logout"));
		this.NavigationMenuLinks = page.locator("ul[class*='navbar'] a");
	}

	public void clickSignupLoginLink() {
		SignupLoginLink.click();
	}
	
	public void DeleteAccount() {
		DeleteAccountLink.click();
	}
	
	public void LogOut() {
		LogOutLink.click();
	}
	
	public String getLoggedInUsername() {	
		String loggedinMessage = page.getByText("Logged in as").textContent().trim();
		return loggedinMessage.substring(loggedinMessage.lastIndexOf(" ") + 1);	
	}
	
	@Step("Verify Invalid Email Error on Login")
	public void verifyInvalidLoginError() {
		String errorMessage = page.getByText("Your email or password is incorrect").textContent().trim();
		assertEquals(errorMessage, "Your email or password is incorrect!", "Incorrect Login credentials");
	}
	
	public void clickMenu(String menu) {
		NavigationMenuLinks.filter(new Locator.FilterOptions().setHasText(menu)).click();
		logger.info("Navigating to " + menu);					   
	}
	
	@Step("Verify Subscription Section Header in Home Page")
	public void verifySubscriptionHeaderHomePage() {
		Assert.assertEquals(super.verifySubscriptionHeaderVisibility(), true); 
	}
	
	@Step("Verify Subscription is success from Home Page")
	public void verifySubscriptionSuccessfromHomePage() {
		Assert.assertEquals(super.verifySubscriptionSuccess(), true); 
	}

}
