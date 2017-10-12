package communication;

import shared.User;

public class CurrentlyAvailableClassesRequest extends Update {

	private static final long serialVersionUID = 212682072235844761L;
	User userInformation;
	public CurrentlyAvailableClassesRequest(User userInformation) {
		this.userInformation = userInformation;
	}
}
