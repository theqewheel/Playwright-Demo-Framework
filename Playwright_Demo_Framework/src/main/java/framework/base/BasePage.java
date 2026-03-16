package framework.base;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.logging.LogManager;
import io.qameta.allure.Step;;

public abstract class BasePage {

	protected final Page page;
	protected Logger logger;
	private final Locator ContinueButton;
	protected SoftAssert softAssert;
	private final Locator subscriptionHeader;
	private final Locator subscriptionEmailTextBox;
	private final Locator subscribeButton;
	private final Locator categoryDisplayText;
	private final Locator homeArrowButton;

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
		this.homeArrowButton = page.locator("#scrollUp");
	}

	@Step("Verify the page load with expected URL {expectedURL} and expected Title {expectedTitle}")
	public void verifyPageLoaded(String expectedURL, String expectedTitle) {
		assertThat(page).hasURL(Pattern.compile(expectedURL, Pattern.CASE_INSENSITIVE));
		assertThat(page).hasTitle(Pattern.compile(expectedTitle, Pattern.CASE_INSENSITIVE));
	}

	@Step("Set checkbox - '{checkboxName}' as {shouldBeChecked}")
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

	@Step("Verify the text - '{expectedMessage}' displayed on the page with exactmatch as {exactMatch}")
	public void verifyTextMessageDisplayed(String expectedMessage, Boolean exactMatch) {

		if (exactMatch) {
			try {
				assertThat(page.getByText(expectedMessage).first()).isVisible();
			} catch (Exception e) {
				softAssert.fail("Text Message not displayed - " + expectedMessage);
			}
		} else {
			try {
				assertThat(page.getByText(Pattern.compile(expectedMessage)).first()).isVisible();
			} catch (Exception e) {
				softAssert.fail("Text Message not displayed - " + expectedMessage);
			}
		}
	}

	@Step("Click continue button")
	public void clickContinue() {
		ContinueButton.click();
	}

	@Step("Subscribe to the page with email - {email}")
	public void subscribe(String email) {
		subscriptionEmailTextBox.fill(email);
		subscribeButton.click();
	}
	
	@Step("Verify 'Subscription' header visibility")
	public Boolean verifySubscriptionHeaderVisibility() {
	 return subscriptionHeader.isVisible();
	}
	
	@Step("Verify subscription success message is visible")
	public Boolean verifySubscriptionSuccess() {
		return page.getByText("You have been successfully subscribed!").isVisible();
	}
	
	@Step("Get the navigation category displayed")
	public String getCategoryNavigationDisplayed() {
		return categoryDisplayText.textContent().trim();
	}
	
	public void scrollToPageBottomUsingKeyboard() {
		page.keyboard().press("End");
	}
	
	public void scrollToPageTopUsingKeyboard() {
		page.keyboard().press("Home");
	}
	
	public void scrollToPageTopUsingArrowKey() {
		homeArrowButton.click();
	}
	
	public void scrollToPageTopUsingEval() {
		page.evaluate("window.scrollTo(0, 0)");
		page.waitForTimeout(2000); //wait for smooth scroll to finish - optional for demo
	}
	
	public void scrollToPageBottomUsingEval() {
		page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
		page.waitForTimeout(2000); //wait for smooth scroll to finish - optional for demo
	}
	
	public void scrollIntoViewOfElement(Locator locatorToView) {
		locatorToView.scrollIntoViewIfNeeded();
	}
	
	public void scrollIntoViewOfElementUsingEval(Locator locatorToView) {
		page.evaluate("document.querySelector('#footer').scrollIntoView()");
	}
	
	public void waitUntilPageLoadCompletes() {
		page.waitForLoadState();
	}
}
