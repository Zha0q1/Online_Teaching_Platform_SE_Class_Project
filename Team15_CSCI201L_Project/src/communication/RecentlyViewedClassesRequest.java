package communication;

import shared.User;

public class RecentlyViewedClassesRequest extends Update{

	private static final long serialVersionUID = -587111107617460895L;
	User userInformation;
	
	public RecentlyViewedClassesRequest(User userInformation) {
		this.userInformation = userInformation;
	}
}
