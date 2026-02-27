package framework.base;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.logging.LogManager;;

public abstract class BasePage {

	protected final Page page;
	protected Logger logger;
	private final Locator ContinueButton;

	public BasePage(Page page) {
		this.page = page;
		this.logger = LogManager.getLogger(this.getClass());
		this.ContinueButton = page.getByRole(AriaRole.LINK,
				new Page.GetByRoleOptions().setName(Pattern.compile("Continue", Pattern.CASE_INSENSITIVE)));

	}

	public void verifyPageLoaded(String expectedURL, String expectedTitle) {
		assertThat(page).hasURL(Pattern.compile(expectedURL, Pattern.CASE_INSENSITIVE));
		assertThat(page).hasTitle(Pattern.compile(expectedTitle));
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
			assertThat(page.getByText(expectedMessage)).isVisible();
		} else {
			assertThat(page.getByText(Pattern.compile(expectedMessage))).isVisible();
		}
	}

	public void ClickContinue() {
		ContinueButton.click();
	}

}
