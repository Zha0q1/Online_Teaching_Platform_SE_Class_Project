package communication;

public class ActivityListRequest extends Update{
	
	private static final long serialVersionUID = 5234225164964512529L;
	
	private int userID;
	
	public ActivityListRequest(int userID){
		this.userID = userID;
	}
	
	public int getUserID() {
		return userID;
	}
}
