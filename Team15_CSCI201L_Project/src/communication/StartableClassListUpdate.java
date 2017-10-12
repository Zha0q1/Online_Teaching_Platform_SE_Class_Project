package communication;

import java.util.Map;

public class StartableClassListUpdate extends Update{

	private static final long serialVersionUID = 7844898388715162179L;
	
	private Map<Integer, String> list;
	
	public StartableClassListUpdate(Map<Integer, String> list) {
		this.list = list;
	}
	
	public Map<Integer, String> getStartableClassList(){
		return list;
	}
	
}
