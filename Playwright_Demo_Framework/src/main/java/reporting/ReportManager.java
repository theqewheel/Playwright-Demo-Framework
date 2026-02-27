package reporting;

import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;

public class ReportManager {
	
	public static void attachScreenshot(String name, byte[] screenshot) {
		Allure.addAttachment(name,new ByteArrayInputStream(screenshot));
	}
	
	public static void logStep(String message) {
		Allure.step(message);
	}

}
