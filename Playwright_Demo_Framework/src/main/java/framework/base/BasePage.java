package framework.base;

import java.util.regex.Pattern;

import com.microsoft.playwright.Page;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;;

public abstract class BasePage {
	
	protected Page page;

	public BasePage(Page page) {
		this.page = page;
	}
	
	public void verifyPageLoaded(String expectedURL, String expectedTitle) {
		assertThat(page).hasURL(Pattern.compile(expectedURL,Pattern.CASE_INSENSITIVE));
		assertThat(page).hasTitle(Pattern.compile(expectedTitle));
	}
	
}
