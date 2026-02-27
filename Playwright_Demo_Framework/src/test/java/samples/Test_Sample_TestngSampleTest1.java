package samples;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class Test_Sample_TestngSampleTest1 {

	@BeforeMethod (groups = {"smoke"})
	public void beforeMethod() {
		System.out.println("Running: TestngSampleTest1 - Before Method");
	}

	@Test (priority = 2, groups = {"smoke"})
	public void testcase_03() {
		System.out.println("Running: TestngSampleTest1 - testcase_03");
	}

	@Test (priority = 1, groups = {"smoke"})
	public void testcase_04() {
		System.out.println("Running: TestngSampleTest1 - testcase_04");
	}

	@AfterMethod (groups = {"smoke"})
	public void afterMethod() {
		System.out.println("Running: TestngSampleTest1 - After Method");
	}
}
