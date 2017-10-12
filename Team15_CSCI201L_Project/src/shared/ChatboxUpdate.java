package shared;


public class ChatboxUpdate extends ClassroomFeatureUpdate{

	private static final long serialVersionUID = -614774891259261249L;

	//
	// Member Variables:
	//
	
	public final String message;
	
	//
	// Methods:
	//
	
	// If 'to' is null, the message is for the whole classroom.
	public ChatboxUpdate(String message, User from, User to) {	
		
		this.message = message;
		
		sentByID = from.getID();
		sentToAll = (to == null);
		sentToID = sentToAll ? -1 : to.getID();
	}
	
	// A message for the whole classroom.
	public ChatboxUpdate(String message, User from) {
		this(message, from, null);
	}
}
