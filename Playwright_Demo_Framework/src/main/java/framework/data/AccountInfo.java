package framework.data;

public class AccountInfo {

	/**
	 * POJO Class - Plain Old Java Class
	 * Data Holder - no code logic implemented
	 * Just Variables + Constructor + Getter
	 * This can be used for creating user dat for filling sign-up forms
	 */
	
	private String username;
	private String email;
	private String password;
	
	public AccountInfo(String username, String password, String email) {
		this.username = username;
		this.email = email;
		this.password = password;
	}
	
	/*
	 * Getters
	 */
	
	public String getUsername() {return username;}
	public String getPassword() {return password;}
	public String getEmail() {return email;}
	
	
}
