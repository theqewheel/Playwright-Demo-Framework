package pages.ae;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.util.Random;
import java.util.regex.Pattern;

import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import framework.data.SignupData;
import framework.utils.StringUtil;
import io.qameta.allure.Step;

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
	
	public SignupDetailPage(Page page,SoftAssert softAssert) {
		super(page,softAssert);
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

	@Step("Select gender as '{gender}'")
	public void selectGender(String gender) {
		if(gender.equalsIgnoreCase("male") || gender.equalsIgnoreCase("m")) {
			MaleRadioButton.click();
		}
		else if (gender.equalsIgnoreCase("female") || gender.equalsIgnoreCase("f")) {
			FemaleRadioButton.click();
		}
	}
	
	@Step("Enter password ********")
	public void enterPassword(String password) {
		PasswordTextbox.fill(password);
	}
	
	@Step("Select date of birth as day-month-year '{day}-{month}-{year}'")
	public void selectDateOfBirth(String day, String month, String year) {
		DaysDropdown.selectOption(day);
		MonthsDropdown.selectOption(month);
		YearsDropdown.selectOption(year);
	}
	
	@Step("Set the checkbox for Opt new letter as : '{opt}'")
	public void optNewsLetter(boolean opt) {
		 setCheckBox(NewsletterCheckbox, opt, "Newsletter");
	}
	
	@Step("Set the checkbox for Opt new letter as : '{opt}'")
	public void optSpecialOffers(boolean opt) {
		 setCheckBox(SpecialOffersCheckbox, opt, "Special Offers");
	}
	
	@Step("Enter firstname - '{}' and lastname - '{}'")
	public void enterFirstAndLastName(String firstName,String lastName) {
		FirstNameTextbox.fill(firstName);
		LastNameTextbox.fill(lastName);
	}
	
	@Step("Enter company name - '{company}'")
	public void enterCompany(String company) {
		CompanyTextbox.fill(company);
	}
	
	@Step("Enter address line 1 - '{address}'")
	public void enterAddress(String address) {
		AddressTextbox.fill(address);
	}
	
	@Step("Enter address line 2 - '{address2}'")
	public void enterAddress2(String address2) {
		Address2Textbox.fill(address2);
	}
	
	@Step("Select country - '{country}'")
	public void selectCountry(String country) {
		CountryDropdown.selectOption(country);
	}
	
	@Step("Enter state - '{state}'")
	public void enterState(String state) {
		StateTextbox.fill(state);
	}
	
	@Step("Enter city - '{city}'")
	public void enterCity(String city) {
		CityTextbox.fill(city);
	}
	
	@Step("Enter zipcode - '{zipcpde}'")
	public void enterZipcode(String zipcode) {
		ZipcodeTextbox.fill(zipcode);
	}
	
	@Step("Enter mobile number - '{mobileNumber}'")
	public void enterMobileNumber(String mobileNumber) {
		MobileNumberTextbox.fill(mobileNumber);
	}
	
	@Step("Click create account button")
	public void clickCreateAccount() {
		CreateAccountButton.click();
	}
	
	@Step("Verify Page Header - Account & Address info is displayed")
	public void verifyPageHeadersDisplayed() {
		try {
			captureScreenshot();
			assertThat(PageHeaderAccountInfo).isVisible();
			assertThat(PageHeaderAddressInfo).isVisible();
		}catch(Exception e) {
			softAssert.fail("Page Headers not displayed - Account/Address");
		}
	}
	
	@Step("Verify auto-populated name '{expectedName}' and email '{expectedEmail}' on Sign-Up Details Page")
	public void verifyAutoPopulatedNameAndEmail(String expectedName, String expectedEmail) {
		try {
			assertThat(NameTextbox).hasAttribute("value", expectedName);
			assertThat(EmailTextbox).hasAttribute("value", expectedEmail);
		}catch(Exception e) {
			softAssert.fail("Missing Auto-populated fields - Name/Email");
		}
	}
	
	@Step("Enter account information for signup with Faker Library")
	public void enterFakerAccountInformation(SignupData data) {
		selectGender(data.getPersonalInfo().getGender());
		enterPassword(data.getAccountInfo().getPassword());
		selectDateOfBirth(String.valueOf(data.getPersonalInfo().getDob().getDayOfMonth()),
				StringUtil.capitalizeFirst(String.valueOf(data.getPersonalInfo().getDob().getMonth())),
				String.valueOf(data.getPersonalInfo().getDob().getYear()));
		optNewsLetter(new Random().nextBoolean());
		optSpecialOffers(new Random().nextBoolean());
		enterFirstAndLastName(data.getPersonalInfo().getFirstName(), data.getPersonalInfo().getLastName());
		captureScreenshot();
	}
	
	@Step("Enter address information for signup with Faker Library")
	public void enterFakerAddressInformation(SignupData data) {
		enterCompany(data.getAddressInfo().getCompany());
		enterAddress(data.getAddressInfo().getAddress1());
		enterAddress2(data.getAddressInfo().getAddress2());
		try {	
			selectCountry(data.getAddressInfo().getCountry());
		}catch(Exception e) {		
			selectCountry("India");			//defaulting to a value
		}
		enterState(data.getAddressInfo().getState());
		enterCity(data.getAddressInfo().getCity());
		enterZipcode(data.getAddressInfo().getZip());
		enterMobileNumber(data.getAddressInfo().getPhoneNumber());
		captureScreenshot();
	}
	
}
