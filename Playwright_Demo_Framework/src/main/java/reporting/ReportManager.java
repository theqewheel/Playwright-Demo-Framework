package reporting;

import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.slf4j.Logger;

import framework.logging.LogManager;

public class ReportManager {
	
	protected static Logger logger = LogManager.getLogger(ReportManager.class);
	
	public static void attachScreenshot(String name, byte[] screenshot) {
		Allure.addAttachment(name,new ByteArrayInputStream(screenshot));
	}
	
	public static void logStep(String message) {
		Allure.step(message);
	}
	
	public static void addAttachement(String filename, String fileType, Path savePath, String fileExtension){
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

}
