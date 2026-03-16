package framework.factory;

import framework.data.SignupData;

public class SignupFactory {

	/** This factory Class is responsible for creating Objects
	 *  This creates Objects for the following:
	 *  	- Fake User Data with Faker Library
	 *  	- In-Valid User Data with Random values
	 *  	- Fake User Data with Time Stamp appends
	 */
	
	public static SignupData createSignupData() {
		
		return new SignupData(
				 AccountFactory.createRandomAccount(),
				 PersonalFactory.createRandomPersonalInfo(),
				 AddressFactory.createRandomAddress()
				);
	}
	
}
