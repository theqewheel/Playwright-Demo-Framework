package test;

import org.testng.annotations.Test;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;

import base.Basetest;

public class Test_CodeGen extends Basetest {

	@Test
	void test() {

		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search")).click();
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search")).fill("quality engineering");
		page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search")).click();
	}
}