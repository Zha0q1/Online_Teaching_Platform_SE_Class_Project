package shared;

import java.awt.Color;
import java.util.Vector;

import client.WhiteboardPanel;

public class Whiteboard extends ClassroomFeature<WhiteboardPanel, WhiteboardUpdate> {

	private static final long serialVersionUID = 8903723779438824284L;
	
	//
	// Member Variables:
	//
	
	// TODO: whatever data represents the current state of the Whiteboard goes here.
	
	public Vector<Pair<Color, Vector<Pair<Integer, Integer>>>> lines = new Vector<Pair<Color, Vector<Pair<Integer, Integer>>>>();
	
	//
	// Methods:
	//

	@Override
	protected WhiteboardPanel makePanel(Classroom classroom, User user) {
		return new WhiteboardPanel(this, classroom, user);
	}
	
	@Override
	protected void updatePanel(WhiteboardUpdate update) {
		panel.addLineSegment(update.line, update.getSenderID());
	}

	@Override
	protected void updateData(WhiteboardUpdate update) {
		// TODO: update the data which represents the current state of the Whiteboard
		
		lines.add(update.line);
		
	}

}
