package shared;

import client.ChatboxPanel;

public class Chatbox extends ClassroomFeature<ChatboxPanel, ChatboxUpdate> {

	private static final long serialVersionUID = -8968585298248381321L;
	
	//
	// Methods:
	//

	@Override
	protected ChatboxPanel makePanel(Classroom classroom, User user) {
		return new ChatboxPanel(this, classroom, user);
	}
	
	@Override
	protected void updatePanel(ChatboxUpdate update) {
		panel.addMessage(update.getReceiverID(), update.getSenderID(), update.message);
	}

	@Override
	protected void updateData(ChatboxUpdate update) {
		// Maybe just do nothing here: I don't think the Chatbox has a persistent state...?
		
		System.out.println("Updating Chatbox data!");
	}

}
