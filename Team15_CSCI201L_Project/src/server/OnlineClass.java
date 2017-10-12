package server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import communication.ClassroomStudentsUpdate;
import communication.Update;
import shared.Classroom;
import shared.User;

public class OnlineClass{
	//basic
	private Map<Integer,ClassroomUserCommunicator> clients;
	private ClassroomUserCommunicator instructor;
	private Classroom classroom;
	private int classID;
	//
	
	public OnlineClass(ClassroomUserCommunicator instructor, Classroom cr) {
		clients = new HashMap<Integer,ClassroomUserCommunicator>();
		this.instructor = instructor;
		this.classroom = cr;
		classID = cr.getID();
	}
	
	public Classroom getClassroom() {
		return classroom;
	}
	
	public int getClassID(){ 
		return classID;
	}
	
	public void acceptClient(ClassroomUserCommunicator com, int userID){
		synchronized(clients) {
			clients.put(userID, com);
		}
		updateStudentList();
	}
	
	public void clearClassroom() {
		for (Entry<Integer, ClassroomUserCommunicator> entry : clients.entrySet()){
			entry.getValue().setClassID(-1);// unattach the students
		}
		clients.clear();
	}
	
	public void updateStudentList() {
		ArrayList<User> students = new ArrayList<User>();
		synchronized(clients) {
			for (Entry<Integer, ClassroomUserCommunicator> entry : clients.entrySet()){
				students.add(entry.getValue().getUser());
			}
		}
		sendToInstructor(new ClassroomStudentsUpdate(students));
		sendToAll(new ClassroomStudentsUpdate(students));
	}
	
	public void sendToAll (Update u) {
		if(u != null){
			synchronized(clients) {
				for (Entry<Integer, ClassroomUserCommunicator> entry : clients.entrySet()){
					entry.getValue().sendUpdate(u);
				}
			}
		}
	}
	
	public void sendTo(int receiverID, Update u) {
		if(u != null){
			synchronized(clients) {
				clients.get(receiverID).sendUpdate(u);		
			}
		}
	}


	public void removeStudent(int userID) {
		synchronized(clients) {
			clients.remove(userID);
		}
		updateStudentList();
	}

	public void sendToInstructor(Update u) {
		if(u != null){
			instructor.sendUpdate(u);
		}	
	}
	
	
}
