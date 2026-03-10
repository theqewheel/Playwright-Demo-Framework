package base;

import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.asserts.SoftAssert;

import com.microsoft.playwright.Page;

import framework.drivers.DriverManager;
import framework.logging.LogManager;
import pages.ae.CartPage;
import pages.ae.CheckOutPage;
import pages.ae.ContactUsPage;
import pages.ae.HomePage;
import pages.ae.ProductDetailPage;
import pages.ae.ProductsPage;
import pages.ae.SignupDetailPage;
import pages.ae.SignupLoginPage;
import pages.ae.TestCasesPage;

public class BaseTest {
	
	protected Page page;
	protected Logger logger;
	protected SoftAssert softAssert;
	protected DriverManager driver;
	protected HomePage homePage;
	protected SignupLoginPage signupLoginPage;
	protected ContactUsPage contactUsPage;
	protected ProductsPage productsPage;
	protected SignupDetailPage signupDetailPage;
	protected TestCasesPage testCasesPage;
	protected CartPage cartPage;
	protected CheckOutPage checkOutPage;
	protected ProductDetailPage productDetailPage;
	
	@BeforeMethod
	public void setup() {
		
		logger = LogManager.getLogger(this.getClass());
		
		driver = new DriverManager();
		
		driver.initDriver();
		
		page = driver.getPage();
		
		logger.info("Initial SETUP is completed.");
		
		softAssert = new SoftAssert();
		
		//initialize page classes
		homePage = new HomePage(page,softAssert);
		signupLoginPage = new SignupLoginPage(page,softAssert);
		contactUsPage = new ContactUsPage(page,softAssert);
		productsPage = new ProductsPage(page,softAssert);
		signupDetailPage = new SignupDetailPage(page, softAssert);
		testCasesPage = new TestCasesPage(page, softAssert);
		cartPage = new CartPage(page, softAssert);
		checkOutPage = new CheckOutPage(page, softAssert);
		productDetailPage = new ProductDetailPage(page, softAssert);
	}

	@AfterMethod
	public void teardown() {
		
		driver.quitDriver();
		
		logger.info("Driver SHUTDOWN is successfull !");
		
		softAssert.assertAll();
	}
	
	protected byte[] captureScreenshot() {
		return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
	}
	

}
