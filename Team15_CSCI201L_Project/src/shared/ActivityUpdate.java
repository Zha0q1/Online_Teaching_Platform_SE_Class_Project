package shared;

public class ActivityUpdate extends ClassroomFeatureUpdate {

	private static final long serialVersionUID = -8554489482967864914L;
	
	//
	// Member Variables:
	//
	
	// TODO: Whatever data represents an update goes here
	
	//
	// Methods:
	//
	
	public ActivityUpdate(User from) {		
		sentByID = from.getID();
	}
	
}
