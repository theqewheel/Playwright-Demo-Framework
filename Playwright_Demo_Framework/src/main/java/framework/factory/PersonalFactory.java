package framework.factory;

import java.time.LocalDate;
import java.time.ZoneId;

import framework.data.PersonalInfo;
import framework.utils.FakerDataUtil;

public class PersonalFactory {

	/** This factory Class is responsible for creating Objects
	 *  This creates Objects for the following:
	 *  	- Fake User Data with Faker Library
	 *  	- In-Valid User Data with Random values
	 *  	- Fake User Data with Time Stamp appends
	 */
	
	public static PersonalInfo createRandomPersonalInfo() {
		
		var faker = FakerDataUtil.getFaker();
		
		
		LocalDate dob = faker.date()
							 .birthday(18, 70)
							 .toInstant()
							 .atZone(ZoneId.systemDefault())
							 .toLocalDate();
		
		String gender = faker.demographic().sex();
		
		return new PersonalInfo(
				faker.name().firstName(),
				faker.name().lastName(),
				dob,
				gender,
				faker.finance().creditCard().replaceAll("[^0-9]", ""), // removes dashes, keeps only numbers
				faker.name().fullName(),
				String.valueOf(faker.number().numberBetween(100, 999)),
				String.format("%02d", faker.number().numberBetween(1,12)),
				String.valueOf(faker.number().numberBetween(2026, 2035))
				);
	}

}
