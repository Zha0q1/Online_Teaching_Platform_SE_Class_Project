package communication;

import java.util.ArrayList;

import communication.Update;
import shared.User;

public class ClassroomStudentsUpdate extends Update{
	

	private static final long serialVersionUID = 770882612378652048L;
	
	private ArrayList<User> students;
	
	public ClassroomStudentsUpdate(ArrayList<User> students){
		this.students = students;
	}
	
	public ArrayList<User> getStudentList() {
		return students;
	}
}
