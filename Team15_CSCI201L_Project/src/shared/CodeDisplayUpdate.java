package shared;


public class CodeDisplayUpdate extends ClassroomFeatureUpdate {

	private static final long serialVersionUID = 1213664913479434967L;
	private String message;
	public String getMessage(){
		return message;
	}
	//
	// Member Variables:
	//
	
	// TODO: whatever data represents an update goes here
	
	//
	// Methods:
	//
	
	public CodeDisplayUpdate(String message, User from) {
		this.message = message;
		// TODO: Store the data
		
		sentToAll = true;
		sentByID = from.getID();
		sentToID = -1;
	}
}
