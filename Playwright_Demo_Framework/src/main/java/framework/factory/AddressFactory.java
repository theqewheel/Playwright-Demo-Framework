package framework.factory;

import java.util.List;

import framework.data.AccountInfo;
import framework.data.AddressInfo;
import framework.utils.FakerDataUtil;

public class AddressFactory {

	/** This factory Class is responsible for creating Objects
	 *  This creates Objects for the following:
	 *  	- Fake User Data with Faker Library
	 *  	- In-Valid User Data with Random values
	 *  	- Fake User Data with Time Stamp appends
	 */
	
	public static AddressInfo createRandomAddress() {
		
		var faker = FakerDataUtil.getFaker();
		
		// Your allowed list for AE - Automation Exercise
		List<String> allowedCountries = List.of(
				"India",
		        "United States",
		        "Canada", 
		        "Australia",
		        "Israel",
		        "New Zealand",
		        "Singapore"
		);

		// Generate random country from faker
		String country = faker.address().country();

		// If not in allowed list, pick random from list
		if (!allowedCountries.contains(country)) {
		    country = faker.options().option(
		    		"India",
			        "United States",
			        "Canada", 
			        "Australia",
			        "Israel",
			        "New Zealand",
			        "Singapore"
		    );
		}
		
		return new AddressInfo(
				 faker.company().name(),
				 faker.address().streetAddress(),
	             faker.address().secondaryAddress(),
	             country, // custom value obtained based on restrictions for AE
	             faker.address().state(),
	             faker.address().cityName(),
	             faker.number().digits(6),   // zip
	             faker.number().digits(10)   // phone
				);
	}
	
}
