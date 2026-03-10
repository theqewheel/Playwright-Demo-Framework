package framework.base;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.logging.LogManager;
import io.qameta.allure.Step;
import reporting.ReportManager;;

public abstract class BasePage {

	protected final Page page;
	protected Logger logger;
	private final Locator ContinueButton;
	protected SoftAssert softAssert;
	private final Locator subscriptionHeader;
	private final Locator subscriptionEmailTextBox;
	private final Locator subscribeButton;
	private final Locator categoryDisplayText;

	public BasePage(Page page, SoftAssert softAssert) {
		this.page = page;
		this.logger = LogManager.getLogger(this.getClass());
		this.softAssert = softAssert;
		this.ContinueButton = page.getByRole(AriaRole.LINK,
				new Page.GetByRoleOptions().setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)));
		this.subscriptionHeader = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Subscription"));
		this.subscriptionEmailTextBox = page.getByPlaceholder("email address");
		this.subscribeButton = page.locator("#subscribe");
		this.categoryDisplayText = page.locator(".breadcrumbs .active");
	}

	public void verifyPageLoaded(String expectedURL, String expectedTitle) {
		assertThat(page).hasURL(Pattern.compile(expectedURL, Pattern.CASE_INSENSITIVE));
		assertThat(page).hasTitle(Pattern.compile(expectedTitle, Pattern.CASE_INSENSITIVE));
	}

	public void setCheckBox(Locator checkbox, boolean shouldBeChecked, String checkboxName) {

		boolean isChecked = checkbox.isChecked();

		if (isChecked != shouldBeChecked) {
			checkbox.click();
		}

		// final verification to ensure the checkbox is in the expected state
		if (shouldBeChecked) {
			assertThat(checkbox).isChecked();
		} else {
			assertThat(checkbox).not().isChecked();
		}

		logger.info("Checkbox: '" + checkboxName + "' is set to: " + shouldBeChecked);

	}

	public void verifyTextMessageDisplayed(String expectedMessage, Boolean exactMatch) {

		if (exactMatch) {
			try {
				assertThat(page.getByText(expectedMessage)).isVisible();
			} catch (Exception e) {
				softAssert.fail("Text Message not displayed - " + expectedMessage);
			}
		} else {
			try {
				assertThat(page.getByText(Pattern.compile(expectedMessage))).isVisible();
			} catch (Exception e) {
				softAssert.fail("Text Message not displayed - " + expectedMessage);
			}
		}
	}

	public void ClickContinue() {
		ContinueButton.click();
	}

	public void subscribe(String email) {
		subscriptionEmailTextBox.fill(email);
		subscribeButton.click();
	}
	
	public Boolean verifySubscriptionHeaderVisibility() {
	 return subscriptionHeader.isVisible();
	}
	
	public Boolean verifySubscriptionSuccess() {
		return page.getByText("You have been successfully subscribed!").isVisible();
	}
	
	public String getCategoryNavigationDisplayed() {
		return categoryDisplayText.textContent().trim();
	}
}
