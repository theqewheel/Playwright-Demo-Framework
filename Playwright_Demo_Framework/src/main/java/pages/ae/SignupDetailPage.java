package pages.ae;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.regex.Pattern;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;

public class SignupDetailPage extends BasePage {
	
	private final Locator PageHeaderAccountInfo;
	private final Locator MaleRadioButton;
	private final Locator FemaleRadioButton;
	private final Locator NameTextbox;
	private final Locator EmailTextbox;
	private final Locator PasswordTextbox;
	private final Locator DaysDropdown;
	private final Locator MonthsDropdown;
	private final Locator YearsDropdown;
	private final Locator NewsletterCheckbox;
	private final Locator SpecialOffersCheckbox;
	private final Locator PageHeaderAddressInfo;
	private final Locator FirstNameTextbox;
	private final Locator LastNameTextbox;
	private final Locator CompanyTextbox;
	private final Locator AddressTextbox;
	private final Locator Address2Textbox;
	private final Locator CountryDropdown;
	private final Locator StateTextbox;
	private final Locator CityTextbox;
	private final Locator ZipcodeTextbox;
	private final Locator MobileNumberTextbox;
	private final Locator CreateAccountButton;
	
	public SignupDetailPage(Page page) {
		super(page);
		this.PageHeaderAccountInfo = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Account",Pattern.CASE_INSENSITIVE)));
		this.MaleRadioButton = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(Pattern.compile("^\\s*Mr.\\s*$")));
		this.FemaleRadioButton = page.getByRole(AriaRole.RADIO, new Page.GetByRoleOptions().setName(Pattern.compile("^\\s*Mrs.\\s*$")));
		this.NameTextbox = page.locator("#name");
		this.EmailTextbox = page.locator("#email");
		this.PasswordTextbox = page.getByTestId("password");
		this.DaysDropdown = page.getByTestId("days");
		this.MonthsDropdown = page.getByTestId("months");
		this.YearsDropdown = page.getByTestId("years");
		this.NewsletterCheckbox = page.getByText("Sign up for our newsletter!");
		this.SpecialOffersCheckbox = page.getByText("Receive special offers from our partners!");
		this.PageHeaderAddressInfo = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName(Pattern.compile("Address",Pattern.CASE_INSENSITIVE)));
		this.FirstNameTextbox = page.getByTestId("first_name");
		this.LastNameTextbox = page.getByTestId("last_name");
		this.CompanyTextbox = page.getByTestId("company");
		this.AddressTextbox = page.getByTestId("address");
		this.Address2Textbox = page.getByTestId("address2");
		this.CountryDropdown = page.getByTestId("country");
		this.StateTextbox = page.getByTestId("state");
		this.CityTextbox = page.getByTestId("city");
		this.ZipcodeTextbox = page.getByTestId("zipcode");
		this.MobileNumberTextbox = page.getByTestId("mobile_number");
		this.CreateAccountButton = page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName(Pattern.compile("Create Account",Pattern.CASE_INSENSITIVE)));
	}

	public void selectGender(String gender) {
		if(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("m")) {
			MaleRadioButton.click();
		}
		else if (gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("f")) {
			FemaleRadioButton.click();
		}
	}
	
	public void enterPassword(String password) {
		PasswordTextbox.fill(password);
	}
	
	public void selectDateOfBirth(String day, String month, String year) {
		DaysDropdown.selectOption(day);
		MonthsDropdown.selectOption(month);
		YearsDropdown.selectOption(year);
	}
	
	public void optNewsLetter(boolean opt) {
		 setCheckBox(NewsletterCheckbox, opt, "Newsletter");
	}
	
	public void optSpecialOffers(boolean opt) {
		 setCheckBox(SpecialOffersCheckbox, opt, "Special Offers");
	}
	
	public void enterFirstAndLastName(String firstName,String lastName) {
		FirstNameTextbox.fill(firstName);
		LastNameTextbox.fill(lastName);
	}
	
	public void enterCompany(String company) {
		CompanyTextbox.fill(company);
	}
	
	public void enterAddress(String address) {
		AddressTextbox.fill(address);
	}
	
	public void enterAddress2(String address2) {
		Address2Textbox.fill(address2);
	}
	
	public void selectCountry(String country) {
		CountryDropdown.selectOption(country);
	}
	
	public void enterState(String state) {
		StateTextbox.fill(state);
	}
	
	public void enterCity(String city) {
		CityTextbox.fill(city);
	}
	
	public void enterZipcode(String zipcode) {
		ZipcodeTextbox.fill(zipcode);
	}
	
	public void enterMobileNumber(String mobileNumber) {
		MobileNumberTextbox.fill(mobileNumber);
	}
	
	public void clickCreateAccount() {
		CreateAccountButton.click();
	}
	
	public void verifyPageHeadersDisplayed() {
		assertThat(PageHeaderAccountInfo).isVisible();
		assertThat(PageHeaderAddressInfo).isVisible();
	}
	
	public void verifyAutoPopulatedNameAndEmail(String expectedName, String expectedEmail) {
		assertThat(NameTextbox).hasAttribute("value", expectedName);
		assertThat(EmailTextbox).hasAttribute("value", expectedEmail);
	}
}
