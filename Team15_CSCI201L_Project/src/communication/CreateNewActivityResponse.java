package communication;
import shared.Activity;

public class CreateNewActivityResponse extends Update{

	private static final long serialVersionUID = -1145861133128600713L;

	public Activity createdActivity;
	public CreateNewActivityResponse(Activity activityCreated){
		this.createdActivity = activityCreated;
	}
	
}
