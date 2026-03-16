package framework.utils;

import java.util.Map;

public class StringUtil {
	
	/**
	 * This utility class is for all String based operations
	 */

	public static String capitalizeFirst(String input) {

	    if (input == null || input.isEmpty()) {
	        return input;
	    }

	    if (input.length() == 1) {
	        return input.toUpperCase();
	    }

	    return input.substring(0, 1).toUpperCase() +
	           input.substring(1).toLowerCase();
	}
	
	public static StringBuilder formattedStringForMaps(Map<String,String> map) {
		
		StringBuilder sBuilder = new StringBuilder();
		map.forEach((key, value) -> sBuilder.append(String.format("%-20s : %s%n", key, value)));
		return sBuilder;
	}
}
