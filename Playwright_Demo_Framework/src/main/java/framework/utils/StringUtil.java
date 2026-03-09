package framework.utils;

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
	
}
