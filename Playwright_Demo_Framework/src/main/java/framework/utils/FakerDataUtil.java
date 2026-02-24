package framework.utils;

import com.github.javafaker.Faker;

public class FakerDataUtil {
	
	private static final Faker faker = new Faker();
	
	private FakerDataUtil() {
		
	}
	
	public static Faker getFaker() {
		return faker;
	}

}
