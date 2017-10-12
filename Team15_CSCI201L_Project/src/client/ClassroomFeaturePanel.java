package client;

import javax.swing.JPanel;

import shared.*;

abstract public class ClassroomFeaturePanel<Feature extends ClassroomFeature<?, ?>> extends JPanel {

	private static final long serialVersionUID = 7087997608697456885L;
	
	//
	// Member Variables:
	//
	
	// Subclasses which don't want to be displayed in tabs should override this method.
	public boolean isTab() { return true; }
	
	protected Feature feature;
	protected Classroom classroom;
	protected User user;
	
	protected boolean amInstructor() {
		return classroom.isInstructor(user);
	}
	
	//
	// Methods:
	//
	
	protected ClassroomFeaturePanel(Feature feature, Classroom classroom, User user) {
		this.feature = feature;
		this.classroom = classroom;
		this.user = user;
		
		setName(feature.getClass().getSimpleName());
	}

}
