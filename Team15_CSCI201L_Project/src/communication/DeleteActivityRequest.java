package communication;

import shared.Activity;
import shared.User;

public class DeleteActivityRequest extends Update{

	private static final long serialVersionUID = -1143112397001699090L;

	private User userInformation;
	public int  activityID;
	public DeleteActivityRequest(User userInformation, int ID){
		this.userInformation = userInformation;
		this.activityID = ID;
	}
}
