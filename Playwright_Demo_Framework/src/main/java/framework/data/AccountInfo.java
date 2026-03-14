package framework.data;

public class AccountInfo {
	
	/** POJO Class
	 * Simple Plain Old Java Class -  is a data holder with no logic implemented
	 * Just variables + constructor + getters
	 * This is for creating user Data for filling forms - Account Information
	 */

	
	private String userName;
	private String email;
	private String password;
	
	public AccountInfo(String userName, String email, String password) {
		this.userName = userName;
		this.email = email;
		this.password = password;
	}
	
	/* GETTER METHODS
	 */
	
	public String getUsername() {return userName;}
	public String getEmail() {return email;}
	public String getPassword() {return password;}
	
}
