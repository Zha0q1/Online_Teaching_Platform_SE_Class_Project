package communication;

public class InstructorLeaveRequest extends Update{

	private static final long serialVersionUID = 1475316922309666970L;
	
	private boolean saveChanges;

	public InstructorLeaveRequest(boolean sc) {
		this.saveChanges = sc;
	}
	
	public boolean saveChanges(){
		return saveChanges;
	}
}
