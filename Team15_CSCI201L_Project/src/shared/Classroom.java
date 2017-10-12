package shared;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;

import communication.Update;

// In our design, this class was named 'ClassroomContent', but I thought it made more sense to just name it 'Classroom'
// Feel free to change it if you prefer it the other way.
// - Phillip

public class Classroom implements Serializable {

	private static final long serialVersionUID = -7683119022634499833L;
	
	//
	// Member Variables:
	//
	
	// The ID of this classroom in the database.
	// Equals -1 if there is no corresponding record in the database, or the ID is unknown.
	// It is transient so that the ID is never serialized in the database (creating a possible conflict with the auto-incrementing ID field) or sent to the client
	 private int classroomID = -1; 
	
	public int getID() {
		return classroomID;
	}
	
	public void setID(int ID) {
		classroomID = ID;
	}
	
	// The name of the classroom.
	private final String name;
	
	public String getName() {
		return name;
	}
	
	// The instructor of the classroom.
	private final User instructor;
	
	public User getInstructor() {
		return instructor;
	}
	
	public boolean isInstructor(User user) {
		return user.equals(instructor);
	}
	
	// A list of students in the classroom. Before storing this object in the database, this ArrayList should always be cleared.
	transient private ArrayList<User> students = new  ArrayList<User>();
	
	public void setStudentList(ArrayList<User> students) {
		this.students = students;
		this.getChatbox().panel.addUserToInstructorCB(students);
	}
	
	public ArrayList<User> getStudents() {
		if (students == null) {
			students = new  ArrayList<User>();
		}
		return students;
	}
	
	private ArrayList<ClassroomListener> listeners = new ArrayList<ClassroomListener>();
	
	// ClassroomFeatures:
	private Chatbox chatbox;
	private Whiteboard whiteboard;
	private CodeDisplay codeDisplay;
	private Activity activity;
	
	public Chatbox getChatbox() {
		return chatbox;
	}
	
	public Whiteboard getWhiteboard() {
		return whiteboard;
	}
	
	public CodeDisplay getCodeDisplay() {
		return codeDisplay;
	}
	
	public void setActivity(Activity a) {
		activity = a;
	}
	
	public Activity getActivity() {
		return activity;
	}
	
	//
	// Methods:
	//
	
	public Classroom(User instructor, String name, Activity activity) {
		this.instructor = instructor;
		this.name = name;
		this.activity = activity;
		
		chatbox = new Chatbox();
		whiteboard = new Whiteboard();
		codeDisplay = new CodeDisplay();
	}
	
	// Returns a list of this classroom's features
	public List<ClassroomFeature<?, ?>> getFeatures() {
		ArrayList<ClassroomFeature<?, ?>> out = new ArrayList<ClassroomFeature<?, ?>>();
		
		out.add(chatbox);
		out.add(whiteboard);
		out.add(codeDisplay);
		if (activity != null) out.add(activity);
		
		return out;
	}
	
	// Forwards the given update to the first feature returned by getFeatures() which will accept it based on its type
	@SuppressWarnings("unchecked")
	public void applyUpdate(Update update) {
				
		for (ClassroomFeature<?, ?> feature : getFeatures()) {

			ParameterizedType featureType = (ParameterizedType) feature.getClass().getGenericSuperclass();
			Class<?> updateType = (Class<?>)featureType.getActualTypeArguments()[1];
			
			if (updateType.isAssignableFrom(update.getClass())) {
							
				feature.getClass().cast(feature).applyUpdate(update);
				
				return;
			}
		}
		
		// If we reach here, there is nowhere to forward the update! It will be forgotten.
	}
	
	public void addClassroomListener(ClassroomListener listener) {
		listeners.add(listener);
	}
	
	public User getUserByID(int userID){
		if(userID == instructor.getID()){
			return instructor;
		}
		for(User user : students){
			if(userID==user.getID()){
				return user;
			}
		}
		return null;
	}
	
	public User getUserByUsername(String uname){
		if(instructor.getDisplayName().equals(uname)){
			return instructor;
		}
		for(User user : students){
			if(user.getDisplayName().equals(uname))
				return user;
		}
		return null;
	}
	
	//
	// Inner Classes:
	//
	
	public interface ClassroomListener {
		public abstract void classroomInfoUpdated();
	}
}
