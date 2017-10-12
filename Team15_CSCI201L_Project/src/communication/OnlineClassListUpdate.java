package communication;

import java.util.Map;

public class OnlineClassListUpdate extends Update{
	
	private static final long serialVersionUID = 4947480961331618262L;
	
	private Map<Integer, String> list;
	
	public OnlineClassListUpdate(Map<Integer, String> list) {
		this.list = list;
	}
	
	public Map<Integer, String> getOnlineClassList(){
		return list;
	}
	
}
