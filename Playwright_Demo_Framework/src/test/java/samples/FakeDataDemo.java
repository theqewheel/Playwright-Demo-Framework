package samples;

import java.util.Locale;

import com.github.javafaker.Faker;

import framework.factory.AccountFactory;

public class FakeDataDemo {

	public static void main(String[] args) {
		
		Faker faker = new Faker();
		faker = new Faker(new Locale("en-IND"));
		
		String name = faker.name().fullName();
		String firstname = faker.name().firstName();
		//firstname = name.split(" ")[0];
		String phoneNumber = String.valueOf(faker.number().numberBetween(7, 9)) + String.valueOf(faker.number().numberBetween(6, 9)) + faker.number().digits(8);
		//phoneNumber = faker.phoneNumber().cellPhone();
		String address = faker.address().buildingNumber() + "," + faker.address().streetAddress();
		String emaildomain = faker.internet().emailAddress().split("@")[1];
		String email = firstname + "@" + emaildomain;
		
		System.out.println("Faked Data: ");
		System.out.println("name >> " + name);
		System.out.println("firstname >> " + firstname);
		System.out.println("address >> " + address);
		System.out.println("email >> " + email);
		System.out.println("phoneNumber >> " + phoneNumber);
		
		var data = AccountFactory.createRandomAccount();
		System.out.println("username >> " + AccountFactory.createRandomAccount().getUsername());
		
	}
	
	
	

}
