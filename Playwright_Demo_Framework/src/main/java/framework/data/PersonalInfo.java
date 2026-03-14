package framework.data;

import java.time.LocalDate;

public class PersonalInfo {
	
	/** POJO Class
	 * Simple Plain Old Java Class -  is a data holder with no logic implemented
	 * Just variables + constructor + getters
	 * This is for creating user Data for filling forms - Personal Information
	 */

	
	private String firstName;
	private String lastName;
	private LocalDate dob;
	private String gender;
	private String paymentCardNumber;
	private String paymentCardPayeeName;
	private String paymentCardCvv;
	private String paymentCardExpirationMonth;
	private String paymentCardExpirationYear;
	
	public PersonalInfo(String firstName, String lastName, 
			LocalDate dob, String gender, String paymentCardNumber,
			String paymentCardPayeeName, String paymentCardCvv,
			String paymentCardExpirationMonth, String paymentCardExpirationYear) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.dob = dob;
		this.gender = gender;
		this.paymentCardNumber = paymentCardNumber;
		this.paymentCardPayeeName = paymentCardPayeeName;
		this.paymentCardCvv = paymentCardCvv;
		this.paymentCardExpirationMonth = paymentCardExpirationMonth;
		this.paymentCardExpirationYear = paymentCardExpirationYear;
	}
	
	/* GETTER METHODS
	 */
	
	public String getFirstName() {return firstName;}
	public String getLastName() {return lastName;}
	public LocalDate getDob() {return dob;}
	public String getGender() {return gender;}
	public String getpaymentCardNumber() {return paymentCardNumber;}
	public String getpaymentCardPayeeName() {return paymentCardPayeeName;}
	public String getpaymentCardCvv() {return paymentCardCvv;}
	public String getpaymentCardExpirationMonth() {return paymentCardExpirationMonth;}
	public String getpaymentCardExpirationYear() {return paymentCardExpirationYear;}
	
}
