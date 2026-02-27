package pages.ae;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;

public class SignupLoginPage extends BasePage {
	
	
	private final Locator PageHeaderSignup;
	private final Locator PageHeaderLogin;
	private final Locator SignupNameTextbox;
	private final Locator SignupEmailTextbox;
	private final Locator SignupButton;
	private final Locator LoginEmailTextbox;
	private final Locator LoginPasswordTextbox;
	private final Locator LoginButton;
	
	public SignupLoginPage(Page page) {
		super(page);
		this.PageHeaderSignup = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Signup")));
		this.PageHeaderLogin = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("login")));	
		this.SignupNameTextbox = page.getByPlaceholder("Name");
		this.SignupEmailTextbox = page.getByTestId("signup-email");
		this.SignupButton = page.getByTestId("signup-button");
		this.LoginEmailTextbox = page.getByTestId("login-email");
		this.LoginPasswordTextbox = page.getByPlaceholder("Password");
		this.LoginButton = page.getByTestId("login-button");
	}
	
	public void EnterSignupName(String name) {
		SignupNameTextbox.fill(name);
	}
	
	public void EnterSignupEmail(String email) {
		SignupEmailTextbox.fill(email);
	}
	
	public void EnterLoginEmail(String email) {
		LoginEmailTextbox.fill(email);
	}
	
	public void EnterLoginPassword(String password) {
		LoginPasswordTextbox.fill(password);
	}
	
	public void EnterSignupDetails(String name, String email) {
		EnterSignupName(name);
		EnterSignupEmail(email);
	}
	
	public void EnterLoginDetails(String email, String password) {
		EnterLoginEmail(email);
		EnterLoginPassword(password);
	}
	
	public void ClickSignup() {
		SignupButton.click();
	}
	
	public void ClickLogin() {
		LoginButton.click();
	}
	
	public void verifyPageHeader(String pagename, String expectedHeader) {
		
		switch(pagename.toLowerCase()) {
		
		case "signup":
			assertThat(PageHeaderSignup).containsText(expectedHeader);break;
			
		case "login":
			assertThat(PageHeaderLogin).containsText(expectedHeader);break;
			
		default:
			logger.error("Invalid page name: " + pagename);
		}
		
	}
	
	
}
