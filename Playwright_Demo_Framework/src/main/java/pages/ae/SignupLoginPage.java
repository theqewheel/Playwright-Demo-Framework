package pages.ae;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;

import java.util.regex.Pattern;

import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import io.qameta.allure.Step;

public class SignupLoginPage extends BasePage {

	private final Locator PageHeaderSignup;
	private final Locator PageHeaderLogin;
	private final Locator SignupNameTextbox;
	private final Locator SignupEmailTextbox;
	private final Locator SignupButton;
	private final Locator LoginEmailTextbox;
	private final Locator LoginPasswordTextbox;
	private final Locator LoginButton;

	public SignupLoginPage(Page page, SoftAssert softAssert) {
		super(page, softAssert);
		this.PageHeaderSignup = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName(Pattern.compile("Signup")));
		this.PageHeaderLogin = page.getByRole(AriaRole.HEADING,
				new Page.GetByRoleOptions().setName(Pattern.compile("Login")));
		this.SignupNameTextbox = page.getByPlaceholder("Name");
		this.SignupEmailTextbox = page.getByTestId("signup-email");
		this.SignupButton = page.getByTestId("signup-button");
		this.LoginEmailTextbox = page.getByTestId("login-email");
		this.LoginPasswordTextbox = page.getByPlaceholder("Password");
		this.LoginButton = page.getByTestId("login-button");
	}

	@Step("Enter sign-up username - '{name}'")
	public void EnterSignupName(String name) {
		SignupNameTextbox.fill(name);
	}

	@Step("Enter sign-up email - '{email}'")
	public void EnterSignupEmail(String email) {
		SignupEmailTextbox.fill(email);
	}

	@Step("Enter login email - '{email}'")
	public void EnterLoginEmail(String email) {
		LoginEmailTextbox.fill(email);
	}

	@Step("Enter login password - '*******'")
	public void EnterLoginPassword(String password) {
		LoginPasswordTextbox.fill(password);
	}

	@Step("Enter sign-up details")
	public void EnterSignupDetails(String name, String email) {
		EnterSignupName(name);
		EnterSignupEmail(email);
	}

	@Step("Enter login details")
	public void EnterLoginDetails(String email, String password) {
		EnterLoginEmail(email);
		EnterLoginPassword(password);
		captureScreenshot();
	}

	@Step("Click sign up button")
	public void ClickSignup() {
		SignupButton.click();
	}

	@Step("Click login button")
	public void ClickLogin() {
		LoginButton.click();
	}

	@Step("Verify Page Header - is displayed for - '{pagename}' with header - '{expectedHeader}'")
	public void verifyPageHeader(String pagename, String expectedHeader) {

		try {
			captureScreenshot();
			switch (pagename.toLowerCase()) {

			case "signup":
				assertThat(PageHeaderSignup).containsText(expectedHeader);
				break;

			case "login":
				assertThat(PageHeaderLogin).containsText(expectedHeader);
				break;

			default:
				logger.error("Invalid page name: " + pagename);
			}
		} catch (Exception e) {
			softAssert.fail("Missing Page Header for - Sign-up/Login");
		}

	}

	@Step("Verify error message for signing up with existing email")
	public void verifyErrorForExistingEmailSignUp() {

		captureScreenshot();
		if (page.getByText("Email Address already exist!") != null) {

			String errorMsg = page.getByText("Email Address already exist!").textContent().trim();
			String expectedMsg = "Email Address already exist!";

			assertEquals(errorMsg, expectedMsg, "Invalid error message on same email signup");
		}
	}
	

}
