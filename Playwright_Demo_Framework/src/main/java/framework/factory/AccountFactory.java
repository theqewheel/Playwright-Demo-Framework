package framework.factory;

import framework.data.AccountInfo;
import framework.utils.FakerDataUtil;

public class AccountFactory {
	
	/**
	 * Factory class is used for creating Objects
	 * 	- Fake Data for account sign-up information
	 */

	public static AccountInfo createRandomAccount() {
		
		var faker = FakerDataUtil.getFaker();
		
		return new AccountInfo(
				"Autobot_" + faker.name().username(),
				faker.internet().emailAddress(),
				"Pwd@"+ faker.number().digits(5)
				);
	}
}
