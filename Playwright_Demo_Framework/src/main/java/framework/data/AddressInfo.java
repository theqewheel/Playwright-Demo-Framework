package framework.data;

public class AddressInfo {
	
	/** POJO Class
	 * Simple Plain Old Java Class -  is a data holder with no logic implemented
	 * Just variables + constructor + getters
	 * This is for creating user Data for filling forms - Address Information
	 */

	private String company;
	private String address1;
	private String address2;
	private String country;
	private String state;
	private String city;
	private String zip;
	private String phoneNumber;
	
	public AddressInfo(String company, String address1, String address2, String country, String state, String city, String zip, String phoneNumber) {
		this.company = company;
		this.address1 = address1;
		this.address2 = address2;
		this.country = country;
		this.state = state;
		this.city = city;
		this.zip = zip;
		this.phoneNumber = phoneNumber;
	}
	
	/* GETTER METHODS
	 */
	
	public String getCompany() {return company;}
	public String getAddress1() {return address1;}
	public String getAddress2() {return address2;}
	public String getCountry() {return country;}
	public String getState() {return state;}
	public String getCity() {return city;}
	public String getZip() {return zip;}
	public String getPhoneNumber() {return phoneNumber;}
	
}
