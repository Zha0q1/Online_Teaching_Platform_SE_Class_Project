package communication;

public class TabChangeUpdate extends Update {

	private static final long serialVersionUID = 8006784067814051592L;
	
	public int tabIndex;
	public TabChangeUpdate(int index) {
		tabIndex = index;
	}
	
}
