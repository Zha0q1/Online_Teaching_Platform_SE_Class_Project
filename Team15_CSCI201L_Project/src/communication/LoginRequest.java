package communication;

public class LoginRequest extends Update {

	private static final long serialVersionUID = 1726184667571734882L;

	public String username;
	public String password;
	
	public boolean createUser;
	
	public boolean guest = false;
	
	// Guest
	public LoginRequest() {
		guest = true;
	}
	
	// Registered User
	public LoginRequest(String username, String password, boolean createUser) {
		this.username = username;
		this.password = password;
		this.createUser = createUser;
	}
	
}
