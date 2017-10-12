package communication;

import shared.User;

public class LoginResponse extends Update {

	private static final long serialVersionUID = -1727636435321828338L;

	public boolean success;
	
	public User user;
	
	public LoginResponse(boolean s, User u) {
		success = s;
		user = u;
	}
	
}
