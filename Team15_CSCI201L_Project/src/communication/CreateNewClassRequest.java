package communication;

import shared.Classroom;
import shared.User;

public class CreateNewClassRequest extends Update{
	private static final long serialVersionUID = 8669250557270819819L;
	//public User userInformation;
	public Classroom classroomInstance;
	public int activityID;
	
	public CreateNewClassRequest(Classroom classInformation, int activityID) {
		this.classroomInstance = classInformation;
		this.activityID = activityID;
	}
}
