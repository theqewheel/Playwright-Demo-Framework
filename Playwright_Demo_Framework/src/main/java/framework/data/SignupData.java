package framework.data;

public class SignupData {
	
	
	/** POJO Class
	 * Simple Plain Old Java Class -  is a data holder with no logic implemented
	 * Just variables + constructor + getters
	 * This is for creating user Data for filling forms - Sign up Information
	 */
	
	private final AccountInfo accountInfo;
	private final PersonalInfo personalInfo;
	private final AddressInfo addressInfo;
	
	public SignupData(AccountInfo accountInfo, PersonalInfo personalInfo, AddressInfo addressInfo) {
		this.accountInfo = accountInfo;
		this.personalInfo = personalInfo;
		this.addressInfo = addressInfo;
	}
	
	/* GETTERS
	 * 
	 */
	
	public AccountInfo getAccountInfo() { return accountInfo; }
    public PersonalInfo getPersonalInfo() { return personalInfo; }
    public AddressInfo getAddressInfo() { return addressInfo; }
    
}
