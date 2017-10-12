package communication;

import shared.User;

public class StartableClassListRequest extends Update {

	private static final long serialVersionUID = 6066790462953197685L;

	public User user;
	public User getUser() {
		return user;
	}
	
	public StartableClassListRequest(User u) {
		user = u;
	}
	
}
