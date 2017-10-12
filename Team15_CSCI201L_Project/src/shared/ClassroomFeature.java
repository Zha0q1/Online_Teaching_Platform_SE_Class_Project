package shared;

import java.io.Serializable;

import client.ClassroomFeaturePanel;
import client.UserClassroomCommunicator;
import communication.Update;

public abstract class ClassroomFeature<PanelType extends ClassroomFeaturePanel<? extends ClassroomFeature<PanelType, UpdateType>>, UpdateType extends Update> implements Serializable {
	
	private static final long serialVersionUID = -8947580103366322314L;
	
	//
	// Member Variables:
	//
	
	protected transient PanelType panel = null;
		
	//
	// Methods:
	//
	
	// Returns a panel for this feature (either for a student or instructor view)
	public PanelType getPanel(Classroom classroom, User user) {
		
		if (panel == null) panel = makePanel(classroom, user);
		
		return panel;
	}
	
	protected abstract PanelType makePanel(Classroom classroom, User user);
	
	// This method is called by the GUI when there is an update that needs to be pushed to the server.
	public void sendUpdate(UpdateType update) {
		
		UserClassroomCommunicator.getInstance().sendUpdate(update);
		
		updateData(update);
	}
	
	protected void applyUpdate(UpdateType update) {
		if (panel != null ) updatePanel(update);
		updateData(update);
	}
	
	protected abstract void updatePanel(UpdateType update);
	
	protected abstract void updateData(UpdateType update);
	
}
