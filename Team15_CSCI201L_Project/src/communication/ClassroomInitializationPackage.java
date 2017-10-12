package communication;

import shared.Classroom;

public class ClassroomInitializationPackage extends Update {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1038078052386946032L;
	private String instructorName;
	private Classroom classroom;
	
	public ClassroomInitializationPackage(Classroom classroom, String instructorName) {
		this.classroom = classroom;
		this.instructorName = instructorName;
	}
	
	public Classroom getClassroom() {
		return classroom;
	}
	
	public String getInstructorName() {
		return instructorName;
	}
}
