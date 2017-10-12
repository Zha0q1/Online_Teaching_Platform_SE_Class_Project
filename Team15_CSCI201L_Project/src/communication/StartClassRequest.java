package communication;
// sent by client, requesting to create a new classroom thread (the class itself is not necessarily new; it may be from the database)
public class StartClassRequest extends Update{
	private static final long serialVersionUID = 27944497184487419L;

	private int classID;
	//private int activityID;
	
	public StartClassRequest(int classID/*, int activityID*/) {
		this.classID = classID;
		//this.activityID = activityID;
	}
	
	public int getClassID() {
		return classID;
	}
	
	/*
	public int getActivityID() {
		return activityID;
	}*/
}
