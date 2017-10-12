package communication;

import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultListModel;

public class OnlineClassListUpdate2 extends Update{
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8479041297654081958L;
	private DefaultListModel classesList;
	private Vector<Integer> vectorToStoreIDs;
	
	public OnlineClassListUpdate2(Map<Integer, String> onlineClasses) {
		classesList = new DefaultListModel(); 
		vectorToStoreIDs = new Vector<Integer>();
		for (Map.Entry<Integer, String> mapEntry : onlineClasses.entrySet()) {
		    String value = mapEntry.getValue();
		    System.out.println("online class " + value);
		    classesList.addElement(value);
		    vectorToStoreIDs.add(mapEntry.getKey());
		}
	}
	
	public DefaultListModel getModel(){
		return classesList;
	}
	
	public Vector<Integer> getVector() {
		return vectorToStoreIDs;
	}
	
}
