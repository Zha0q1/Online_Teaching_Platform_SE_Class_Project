package server;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import communication.ClassroomInitializationPackage;
import communication.InstructorLeftUpdate;
import communication.UpdateParser;
import shared.Activity;
import shared.Classroom;
import shared.User;

public class ClassServerListener extends Thread{

	// basic 
	private ServerSocket ss;
	private UpdateParser parser;
	private Vector<ClassroomUserCommunicator> communicators;
	private Set<String> loggedInUsers;
	private Map<Integer, String> onlineClassList;
	private Map<Integer, OnlineClass> classes;
	
	// server info
	private int numLoggedinUsers = 0;
	// running?
	private boolean running;
	
	public ClassServerListener(int port) throws IOException {
		this.ss = new ServerSocket(port);
		parser = new UpdateParser(this);
		communicators = new Vector<ClassroomUserCommunicator>();
		onlineClassList = new HashMap<Integer, String>();
		classes = new HashMap<Integer, OnlineClass>() ;
		loggedInUsers = new HashSet<String>();
		running = true;
		start();
	}
	
	public void run() {
		try {
			while (running) {	
				System.out.println("Now "+ getNumOnlineClients() + " users online");
				Socket s = ss.accept();
				System.out.println("Connecting from " + s.getInetAddress());
				ClassroomUserCommunicator com = new ClassroomUserCommunicator(s,parser,this);
				synchronized(communicators) {
					communicators.add(com);
				}
			}
		} catch (IOException ioe) {
			System.out.println("ioe" + ioe.getMessage());
		} finally {
			try {
				if (ss != null) {
					ss.close();
				} 
			} catch (IOException ioe) {}
			
			parser.close();
		}
	}
	
	

	
	public void StartClass(int classID, ClassroomUserCommunicator com) {
		if(!onlineClassList.containsKey(classID)) { // if the class is not already online
			Classroom cr = parser.getDriver().getClassroom(classID);
			synchronized(onlineClassList){
				onlineClassList.put(classID, cr.getName());
			}
			OnlineClass oc = new OnlineClass(com,cr);
			synchronized(classes) { 
				classes.put(classID, oc);
			}
			com.setOnlineClass(oc);
		}
	}
	
	public void directUser(int classID ,ClassroomUserCommunicator com, int userID) {
		com.setClassID(classID);
		System.out.println(com.getUsername()+"'s classid " + classID);
		classes.get(classID).acceptClient(com, userID);
		com.sendUpdate(new ClassroomInitializationPackage(classes.get(classID).getClassroom(),classes.get(classID).getClassroom().getInstructor().getUsername()));
	}
	
	public Map<Integer, String> getOnlineClassList() {
		synchronized(onlineClassList){
		//	System.out.println(onlineClassList.size() +"online on server ");
			return onlineClassList;
		}
	}
	
	public Map<Integer, String> getActivityList(int userID) {
		Map<Integer, String> list = new HashMap<Integer, String>();
//		ArrayList<Classroom> classrooms = parser.getDriver().????(userID);
//		for (Classroom cr : classrooms) {
//			list.put(cr.getID(), cr.getName());
//		}
		return list;
	}
	
	public Map<Integer, String> getStartableClassList(int userID) {
		Map<Integer, String> list = new HashMap<Integer, String>();
		ArrayList<Classroom> classrooms = parser.getDriver().instructorClassroooms(userID);
		for (Classroom cr : classrooms) {
			list.put(cr.getID(), cr.getName());
		}
		return list;
	}
	
	public Map<Integer, String> getInstructorActivities(int userID) {
		Map<Integer, String> list = new HashMap<Integer, String>();
		ArrayList<Activity> activities = parser.getDriver().instructorActivities(userID);
		for (Activity a : activities) {
			list.put(a.getID(), a.getName());
		}
		return list;
	}
	
	public MySQLDriver getDriver() {
		return parser.getDriver();
	}

	public void removeCommunicator(ClassroomUserCommunicator com) {
		synchronized(communicators) {
			communicators.remove(com);
		}
		System.out.println(com.getUsername()+ " exits.");
		System.out.println("Now "+ getNumOnlineClients() + " users online");
		if (com.getUserID() > 0) {// the user is logged in
			numLoggedinUsers --;
			loggedInUsers.remove(com.getUsername());
		}
		if(com.getOnlineClass()!=null) {// the user is an holding a class!
			int classID = com.getOnlineClass().getClassID();
			clearClassroom(com);
			synchronized(classes){
				classes.remove(classID);
			}
			synchronized(onlineClassList){
				onlineClassList.remove(classID);
			}
		}
		if(com.getClassID()!=-1) {// the user is a student in some class!
			synchronized(classes){
				//System.out.println(com.getClass());
				//System.out.println(classes.get(com.getClassID())==null?"its null":"its not null");
				classes.get(com.getClassID()).removeStudent(com.getUserID());
			}
		}
		
	}
	
	public OnlineClass getOnlineClass(int classID) {
		synchronized(classes) {
			if (classes.containsKey(classID)) {
				return classes.get(classID);
			}
			else {
				return null;
			}
		}
	}

	public int getNumLogedinUsers() {
		return numLoggedinUsers;
	}
	
	public int getNumOnlineClients() {
		synchronized(communicators) {
			return communicators.size();
		}
	}
	
	public int getNumGuests() {
		return getNumOnlineClients() - getNumLogedinUsers();
	}
	
	public boolean isLoggedIn(String userName) {
		return loggedInUsers.contains(userName);
	}

	public void addLoggedinUser(User usr) {
		loggedInUsers.add(usr.getUsername());
		numLoggedinUsers ++;
	}

	public void endClass(ClassroomUserCommunicator com, boolean saveChanges) {
		int classID = com.getOnlineClass().getClassID();
		clearClassroom(com);
		if (saveChanges) {
			saveClass(com.getOnlineClass().getClassroom());
		}
		synchronized(classes){
			classes.remove(classID);
		}
		synchronized(onlineClassList){
			onlineClassList.remove(classID);
		}
		com.setOnlineClass(null);
	}
	
	public void clearClassroom(ClassroomUserCommunicator com) {
		com.getOnlineClass().sendToAll(new InstructorLeftUpdate());
		synchronized(classes){
			classes.get(com.getOnlineClass().getClassID()).clearClassroom();
		}
	}

	public void studentLeaveClass(ClassroomUserCommunicator com) {
		synchronized(classes){
			classes.get(com.getClassID()).removeStudent(com.getUserID());
		}
		com.setClassID(-1);
	}
	
	public void saveClass(Classroom cr) {
		parser.getDriver().updateClassroom(cr);
	}

	public Classroom getClassroom(int classID) {
		synchronized(classes){
			if ( classes.containsKey(classID)) {
				return classes.get(classID).getClassroom();
			}
			else {
				return null;
			}
		}
	}
	
	public String getClassroomInstructorName(int classID) {
		synchronized(classes){
			if ( classes.containsKey(classID)) {
				return classes.get(classID).getClassroom().getInstructor().getUsername();
			}
			else {
				return "";
			}
		}
	}
	
}

