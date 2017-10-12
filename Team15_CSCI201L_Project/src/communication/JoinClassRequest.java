package communication;

public class JoinClassRequest extends Update{
	/**
	 * 
	 */
	private static final long serialVersionUID = 7441130839854262221L;
	private int classID;

	public JoinClassRequest(int classID) {
		this.classID = classID;
	}
	
	
	public int getClassID() {
		return classID;
	}
}
