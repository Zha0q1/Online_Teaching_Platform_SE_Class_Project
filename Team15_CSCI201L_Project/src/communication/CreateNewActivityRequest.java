package communication;

import shared.Activity;
import shared.User;

public class CreateNewActivityRequest extends Update{
	private static final long serialVersionUID = -3702996080593196960L;
	public Activity createActivity;
	public User user;
	public CreateNewActivityRequest(Activity createActivity, User u) {
		this.createActivity = createActivity;
		user = u;
	}
}
