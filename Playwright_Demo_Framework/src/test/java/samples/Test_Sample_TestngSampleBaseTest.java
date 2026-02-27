package samples;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Test_Sample_TestngSampleBaseTest {

	@BeforeSuite (alwaysRun = true)
	public void beforeSuite() {
		System.out.println("Running: TestngSampleBaseTest - Before Suite");
	}

	@BeforeTest (groups = {"smoke"})
	public void beforeTest() {
		System.out.println("Running: TestngSampleBaseTest - Before Test");
	}

	@BeforeClass (groups = {"regression"})
	public void beforeClass() {
		System.out.println("Running: TestngSampleBaseTest - Before Class");
	}

	@BeforeMethod (onlyForGroups = {"smoke"})
	public void beforeMethod() {
		System.out.println("Running: TestngSampleBaseTest - Before Method");
	}

	@Test (groups = {"smoke"})
	public void testcase_01() {
		System.out.println("Running: TestngSampleBaseTest - testcase_01");
	}

	@Test (enabled = true, groups = {"samples"})
	public void testcase_02() {
		System.out.println("Running: TestngSampleBaseTest - testcase_02");
	}

	@AfterMethod (groups = {"smoke","regression1"})
	public void afterMethod() {
		System.out.println("Running: TestngSampleBaseTest - After Method");
	}

	@AfterClass (groups = {"regression"})
	public void afterClass() {
		System.out.println("Running: TestngSampleBaseTest - After Class");
	}

	@AfterTest (groups = {"smoke"})
	public void afterTest() {
		System.out.println("Running: TestngSampleBaseTest - After Test");
	}

	@AfterSuite (alwaysRun = true)
	public void afterSuite() {
		System.out.println("Running: TestngSampleBaseTest - After Suite");
	}

}
