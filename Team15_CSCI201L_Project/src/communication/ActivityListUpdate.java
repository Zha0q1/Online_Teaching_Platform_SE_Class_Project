package communication;

import java.util.Map;

public class ActivityListUpdate extends Update {
	
	private Map<Integer,String> list;
	
	public ActivityListUpdate(Map<Integer,String> list) {
		this.list = list;
	}
	
	public Map<Integer,String> getActivityList() {
		return list;
	}
}
