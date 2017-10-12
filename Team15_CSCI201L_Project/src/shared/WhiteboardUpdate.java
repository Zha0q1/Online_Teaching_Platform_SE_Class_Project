package shared;

import java.awt.Color;
import java.util.Vector;

import communication.Update;

public class WhiteboardUpdate extends ClassroomFeatureUpdate {

	private static final long serialVersionUID = 5672766376441384243L;
	
	//
	// Member Variables:
	//
	Pair<Color, Vector<Pair<Integer, Integer>>> line;
	
	
	// TODO: Whatever data represents an update goes here
	
	//
	// Methods:
	//
	
	public WhiteboardUpdate(Pair<Color, Vector<Pair<Integer, Integer>>> updateLine, User from) {
		
		line = updateLine;
		
		sentToAll = true;
		sentByID = from.getID();
		sentToID = -1;
	}
}
