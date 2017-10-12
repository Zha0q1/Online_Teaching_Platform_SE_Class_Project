package communication;

import client.UserClassroomCommunicator;
import server.ClassServerListener;
//import server.ClassroomStudentsUpdate;
import server.ClassroomUserCommunicator;
import server.MySQLDriver;
import shared.Activity;
import shared.ActivityUpdate;
import shared.ChatboxUpdate;
import shared.ClassroomFeatureUpdate;
import shared.User;

public class UpdateParser {
	
	private MySQLDriver mysql = null;
	
	// for server
	private ClassServerListener listener;
	public UpdateParser(ClassServerListener csl) {
		listener = csl;
		
		mysql = new MySQLDriver();	
		mysql.connect();
	}
	
	// for client
	// pointers to user's GUI and class content
	//
	public UpdateParser() {
		
	}
	
	// for both
	public void close() {
		if (mysql != null) mysql.stop();
	}
	
	// for server
	public void parse(Update u, ClassroomUserCommunicator com) {
		if (com.getOnlineClass()!=null) {
			//if an instructor sends a chatbox update
			if (u instanceof ChatboxUpdate) {
				if (u.sentToAll) {
					com.getOnlineClass().sendToInstructor(u);
					com.getOnlineClass().sendToAll(u);
				}
				else {
					com.getOnlineClass().sendToInstructor(u);
					com.getOnlineClass().sendTo(u.getReceiverID(), u);
				}
			}
			else if(u instanceof ClassroomFeatureUpdate) {
				// Broadcast it to all students!
				com.getOnlineClass().sendToAll(u);
				// edit the class locally
				com.getOnlineClass().getClassroom().applyUpdate(u);
			} 
			else if (u instanceof TabChangeUpdate) { // TabChangeUpdate
				com.getOnlineClass().sendToAll(u);
			}
			
		}
		// if a student sends a chatbox update
		if (com.getOnlineClass()==null&&(u instanceof ChatboxUpdate)) {
			if (u.sentToAll) {
				System.out.println(com.getClassID());
				System.out.println(listener.getOnlineClass(com.getClassID())==null?"its null":"its not null");
				listener.getOnlineClass(com.getClassID()).sendToInstructor(u);
				listener.getOnlineClass(com.getClassID()).sendToAll(u);
			}
			else {
				listener.getOnlineClass(com.getClassID()).sendToInstructor(u);
				listener.getOnlineClass(com.getClassID()).sendTo(com.getUserID(), u);
			}
		}
		
		else if(com.getOnlineClass()==null&&(u instanceof ActivityUpdate)){
			listener.getOnlineClass(com.getClassID()).sendToInstructor(u);
		}
		
		if (u instanceof StartClassRequest) { // New Class:
			listener.StartClass(((StartClassRequest) u).getClassID(),com);
			
			StartClassResponse response = new StartClassResponse();
			com.sendUpdate(response);
			
		} else if (u instanceof LoginRequest) { // Handle Login:
			LoginRequest request = (LoginRequest)u;
			
			User usr;
			LoginResponse response;
			// if the user is logged in then deny duplicate requests
			if(listener.isLoggedIn(request.username)) {
				com.sendUpdate(new LoginResponse(false, null));
				return;
			}
			
			if (request.guest) {
				usr = new User();
				response = new LoginResponse(true, usr);
			} else if (request.createUser) { // requesting to create a new user
				usr = mysql.addUser(request.username, request.password);
				response = new LoginResponse(usr  != null, usr);
			} else { // requesting to validate an existing user
				
				usr  = mysql.validateUser(request.username, request.password);
				
				response = new LoginResponse(usr  != null, usr );
			}
			com.sendUpdate(response);//send respond
			if (usr!=null) {
				com.setUser(usr);
				if(!request.guest) {
					listener.addLoggedinUser(usr);
				}
				
			}
			
		} else if (u instanceof CurrentlyAvailableClassesRequest) {
			CurrentlyAvailableClassesRequest request = (CurrentlyAvailableClassesRequest)u;
			
			com.sendUpdate(new OnlineClassListUpdate(listener.getOnlineClassList()));

		} else if (u instanceof CurrentlyAvailableClassesRequest2){
			com.sendUpdate(new OnlineClassListUpdate2(listener.getOnlineClassList()));
		}else if (u instanceof StartableClassListRequest) {
			StartableClassListRequest request = (StartableClassListRequest)u;
			
			com.sendUpdate(new StartableClassListUpdate(listener.getStartableClassList(request.getUser().getID())));
		} else if (u instanceof ActivityListRequest) {
			
			ActivityListRequest request = (ActivityListRequest)u;
			
			com.sendUpdate(new ActivityListUpdate(listener.getInstructorActivities(request.getUserID())));
			
		} else if (u instanceof CreateNewActivityRequest) {
	
			CreateNewActivityRequest request = (CreateNewActivityRequest)u;
			mysql.createActivity(request.createActivity, request.user);
			System.out.println("create activity");
			com.sendUpdate(new CreateNewActivityResponse(request.createActivity));
			
		} else if(u instanceof CreateNewClassRequest) {

			
			
			CreateNewClassRequest request = (CreateNewClassRequest)u;
			System.out.println("activity id in update parser "+ request.activityID);
			Activity a = mysql.getActivity(request.activityID);
			request.classroomInstance.setActivity(a);
			
			mysql.createClassroom(request.classroomInstance);
						
			com.sendUpdate(new CreateNewClassResponse(request.classroomInstance));
			
		} else if (u instanceof DeleteActivityRequest) {
			
			DeleteActivityRequest request = (DeleteActivityRequest)u;
			mysql.deleteActivity(request.activityID);
			

		} else if(u instanceof InstructorLeaveRequest) {
			listener.endClass(com,((InstructorLeaveRequest) u).saveChanges());
		} else if(u instanceof StudentLeaveRequest) {
			listener.studentLeaveClass(com);
		} else if(u instanceof CreateNewActivityRequest) {
			//mysql.createActivity(((CreateNewActivityRequest) u).createActivity);
			//com.sendUpdate(new CreateNewActivityResponse(((CreateNewActivityRequest)u).createActivity));
		} else if(u instanceof ClassroomInitializationPackageRequest) {
			com.sendUpdate(new ClassroomInitializationPackage(listener.getClassroom(((ClassroomInitializationPackageRequest) u).getClassID()),listener.getClassroomInstructorName(((ClassroomInitializationPackageRequest) u).getClassID())));
		} else if (u instanceof JoinClassRequest) {
			listener.directUser(((JoinClassRequest) u).getClassID(), com, com.getUserID());
		}
		
	}
	
	// for client
	public void parse(Update u, UserClassroomCommunicator com) {
		
		if (com.classroom!=null) {
			if(u instanceof ClassroomFeatureUpdate) {
				// edit the class locally
												
				com.classroom.applyUpdate(u);
			} else if (u instanceof TabChangeUpdate) {
				
				TabChangeUpdate update = (TabChangeUpdate)u;
				
				com.classroomGUI.tabPanel.suggestTabIndex(update.tabIndex);
			}
			else if (u instanceof ClassroomStudentsUpdate) {
				com.classroom.setStudentList(((ClassroomStudentsUpdate) u).getStudentList());
				
				com.classroomGUI.updateStudentList();
			}
			else if (u instanceof InstructorLeftUpdate) {
				com.classroomGUI.InstructorLeft();
			}
			else if (u instanceof OnlineClassListUpdate2) {
				System.out.println("receive update2!!");
				com.homeScreenGUI.refreshClassList(((OnlineClassListUpdate2) u).getModel(),((OnlineClassListUpdate2) u).getVector());
				
			}
		}
	}
	
	public MySQLDriver getDriver() {
		return mysql;
	}
}
