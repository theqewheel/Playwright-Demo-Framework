package reporting;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import framework.logging.LogManager;
import framework.logging.UniversalLogger;
import io.qameta.allure.Allure;

public class ReportManager {
	
	protected static UniversalLogger logger = LogManager.getLogger(ReportManager.class);
	
	public static void attachScreenshot(String name, byte[] screenshot) {
		Allure.addAttachment(name,new ByteArrayInputStream(screenshot));
	}
	
	public static void logStep(String message) {
		Allure.step(message);
	}
	
	public static void addFileAttachement(String filename, String fileType, Path savePath, String fileExtension){
		try {
			Allure.addAttachment(
					filename, 
					fileType, 
					Files.newInputStream(savePath),
					fileExtension);
			logger.info("✅ Attachment added to Allure: {}", filename);
		} catch (IOException e) {
			logger.error("Failed to attach file to Allure: {}", e.getMessage());
			e.printStackTrace();
		} 
	}
	
	public static void addParameter(String name, Object value) {
		Allure.parameter(name, value);
	}

	public static void attachTextContentAsSection(String contentName, String contentString) {
		Allure.addAttachment(contentName, "text/plain", contentString, ".txt");
	}
}
