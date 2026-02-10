package test;

import org.testng.annotations.Test;

public class TestngSampleTest2 extends TestngSampleBaseTest {

	@Test (groups = {"regression1"})
	public void testcase_05() {
		System.out.println("Running: TestngSampleTest2 - testcase_05");
	}

	@Test (groups = {"regression1"}, invocationCount = 3, description = "This is a sample test case with invocation count")
	public void testcase_06() {
		System.out.println("Running: TestngSampleTest2 - testcase_06");
	}

}
