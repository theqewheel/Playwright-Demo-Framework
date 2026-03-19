package pages.ae;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import framework.base.BasePage;
import io.qameta.allure.Step;
import reporting.ReportManager;

public class ContactUsPage extends BasePage {

	private final Locator PageHeader1;
	private final Locator PageHeader2;
	private final Locator NameTextBox;
	private final Locator EmailTextBox;
	private final Locator SubjectTextBox;
	private final Locator MessagerTextArea;
	private final Locator ChooseFileButton;
	private final Locator SubmitButton;
	private final Locator SuccessMessage;
	private final Locator HomeButton;

	public ContactUsPage(Page page, SoftAssert softAssert) {
		super(page,softAssert);
		this.PageHeader1 = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("CONTACT US"));
		this.PageHeader2 = page.getByRole(AriaRole.HEADING, new Page.GetByRoleOptions().setName("Get In Touch"));
		this.NameTextBox = page.getByTestId("name");
		this.EmailTextBox = page.getByTestId("email");
		this.SubjectTextBox = page.getByTestId("subject");
		this.MessagerTextArea = page.getByTestId("message");
		this.ChooseFileButton = page.locator("input[name='upload_file']");
		this.SubmitButton = page.getByTestId("submit-button");
		this.SuccessMessage = page.locator("div[class*='status']");
		this.HomeButton = page.locator("a[class*='btn-success']");
	}

	@Step("Enter name in the contact form - '{name}'")
	public void enterName(String name) {
		NameTextBox.fill(name);
	}

	@Step("Enter email in the contact form - '{email}'")
	public void enterEmail(String email) {
		EmailTextBox.fill(email);
	}

	@Step("Enter subject in the contact form - '{subject}'")
	public void enterSubject(String subject) {
		SubjectTextBox.fill(subject);
	}

	@Step("Enter query in the contact form - '{query}'")
	public void enterQuery(String queryMessage) {
		MessagerTextArea.fill(queryMessage);
	}

	@Step("Upload file from project directory in the contact form")
	public void uploadFileFromProjectDirectory(String filenameWithExtension) {
		Path filePath = Paths.get("src/test/resources/"+filenameWithExtension);
		ChooseFileButton.setInputFiles(filePath);
		ReportManager.addParameter("File Uploaded", filenameWithExtension);
	}
	
	@Step("Upload file from local directory in the contact form")
	public void uploadFileFromLocalDirectory(String fileAbsolutePath) {
		Path filePath = Paths.get(fileAbsolutePath);
		ChooseFileButton.setInputFiles(filePath);
	}

	@Step("Click submit button for the contact form submission")
	public void clickSubmit() {
		page.onDialog(dialog -> {
			logger.info("Accepting the Dialog Alert: " + dialog.message());
			dialog.accept();
		});
		SubmitButton.click();
		page.waitForLoadState();
	}
	
	@Step("Verify Contact Us Form submission is success")
	public void verifySuccessMessage() {
		captureScreenshot();
		String message = SuccessMessage.textContent().trim();
		assertEquals(message, "Success! Your details have been submitted successfully.","Invalid Success Message");
	}
	
	@Step("Fill the contact us form with name-{name}, email-{email}, subject-{subject}, query-{queryMessage}")
	public void fillForm(String name, String email, String subject, String queryMessage) {
		enterName(name);
		enterEmail(email);
		enterSubject(subject);
		enterQuery(queryMessage);
		captureScreenshot();
	}
	
	@Step("Click Home button")
	public void clickHome() {
		HomeButton.click();
	}
	
	@Step("Verify Page Header - CONTACT US")
	public void verifyPageHeader1() {
		try {
			assertTrue(PageHeader1.isVisible());
		}catch(Exception e) {
			softAssert.fail("Page Header - CONTACT US not visible");
		}
		
	}
	
	@Step("Verify Page Header - Get In Touch")
	public void verifyPageHeader2() {
		try {
			assertTrue(PageHeader2.isVisible());
		}catch(Exception e) {
			softAssert.fail("Page Header - Get in Touch not visible");
		}
	}

}
