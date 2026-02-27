package pages.ae;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;

public class HomePage extends BasePage {
	
	private final Locator SignupLoginLink;
	private final Locator DeleteAccountLink;
	
	public HomePage(Page page) {
		super(page);
		this.SignupLoginLink = page.getByText(Pattern.compile("Signup",Pattern.CASE_INSENSITIVE));
		this.DeleteAccountLink = page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName(Pattern.compile("Delete Account",Pattern.CASE_INSENSITIVE)));
	}

	public void clickSignupLoginLink() {
		SignupLoginLink.click();
	}
	
	public void DeleteAccount() {
		DeleteAccountLink.click();
	}
	
	
}
