package shared;

import client.ActivityPanel;

public class Activity extends ClassroomFeature<ActivityPanel, ActivityUpdate> {

	private static final long serialVersionUID = -6664270938390102152L;
	private String name;
	private int activityID = -1;
	private String skeletonCode;
	private String inputValues;
	private String expectedOutput;
	private User userInformation;
	private int mode;
	
	
	// TODO: whatever data represents an activity goes here
	public Activity(String skeletonCode, User userInformation, String inputValues, String expectedOutput, String name, int mode){
		this.skeletonCode = skeletonCode;
		this.inputValues = inputValues;
		this.expectedOutput = expectedOutput;
		this.userInformation = userInformation;
		this.name = name;
		this.mode = mode;
	}
	

	public void setID(int id){
		this.activityID = id;
	}
	
	public String getName(){
		return name;
	}
	
	public String getSkeletonCode(){
		return skeletonCode;
	}
	
	public String getInput(){
		return inputValues;
	}
	
	public String getOutput(){
		return expectedOutput;
	}
	
	
	public User returnUser(){
		return userInformation;
	}
	@Override
	protected ActivityPanel makePanel(Classroom classroom, User user) {
		return new ActivityPanel(this, classroom, user);
	}
	
	@Override
	protected void updatePanel(ActivityUpdate update) {
		System.out.println("Recieved completed activity update");
		panel.addToCompletedStudents(update.getSenderID());
	}

	@Override
	protected void updateData(ActivityUpdate update) {
		// TODO: update the data		
	}
	
	public int getID() {
		return activityID; 
	}
	
	public int getMode(){
		return mode;
	}

}
