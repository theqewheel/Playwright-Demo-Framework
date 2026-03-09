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
	protected static HomePage homePage;
	protected static SignupLoginPage signupLoginPage;
	protected static ContactUsPage contactUsPage;
	protected static ProductsPage productsPage;
	protected static SignupDetailPage signupDetailPage;
	protected static TestCasesPage testCasesPage;
	protected static CartPage cartPage;
	protected static CheckOutPage checkOutPage;
	protected static ProductDetailPage productDetailPage;
	
	@BeforeMethod
	public void setup() {
		
		logger = LogManager.getLogger(this.getClass());
		
		DriverManager.initDriver();
		
		page = DriverManager.getPage();
		
		logger.info("Initial SETUP is completed.");
		
		softAssert = new SoftAssert();
		
		//initialize page classes
		if(homePage==null) homePage = new HomePage(page,softAssert);
		if(signupLoginPage==null) signupLoginPage = new SignupLoginPage(page,softAssert);
		if(contactUsPage==null) contactUsPage = new ContactUsPage(page,softAssert);
		if(productsPage==null) productsPage = new ProductsPage(page,softAssert);
		if(signupDetailPage==null) signupDetailPage = new SignupDetailPage(page, softAssert);
		if(testCasesPage==null) testCasesPage = new TestCasesPage(page, softAssert);
		if(cartPage==null) cartPage = new CartPage(page, softAssert);
		if(checkOutPage==null) checkOutPage = new CheckOutPage(page, softAssert);
		if(productDetailPage==null) productDetailPage = new ProductDetailPage(page, softAssert);
	}

	@AfterMethod
	public void teardown() {
		
		DriverManager.quitDriver();
		
		logger.info("Driver SHUTDOWN is successfull !");
		
		softAssert.assertAll();
	}
	
	protected byte[] captureScreenshot() {
		return page.screenshot(new Page.ScreenshotOptions().setFullPage(true));
	}
	
	

}
