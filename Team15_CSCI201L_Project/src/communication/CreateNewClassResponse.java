package communication;

import shared.Classroom;

public class CreateNewClassResponse extends Update{
	/**
	 * 
	 */
	private static final long serialVersionUID = -4062580324235043424L;
	public Classroom classroomInstance;
	public CreateNewClassResponse(Classroom userInformation) {
		this.classroomInstance = userInformation;
	}
}
