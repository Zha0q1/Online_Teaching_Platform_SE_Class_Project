package shared;

import client.CodeDisplayPanel;

public class CodeDisplay extends ClassroomFeature<CodeDisplayPanel, CodeDisplayUpdate> {

	private static final long serialVersionUID = 6774264011847437784L;
	
	//
	// Member Variables:
	private String code;
	//
	
	// TODO: Whatever data represents the current state of the code display goes here
	
	//
	// Methods:
	//

	@Override
	protected CodeDisplayPanel makePanel(Classroom classroom, User user) {
		return new CodeDisplayPanel(this, classroom, user);
	}
	
	@Override
	protected void updatePanel(CodeDisplayUpdate update) {
		// TODO: Call methods on the CodeDisplayPanel this.panel, which is guaranteed to be non-null
		panel.addText(update.getReceiverID(),update.getSenderID(),update.getMessage());
	}

	@Override
	protected void updateData(CodeDisplayUpdate update) {
		// TODO: update the data which represents the current state of the CodeDisplay
		code = update.getMessage();
		//System.out.println("Updating CodeDisplay data!");
	}	
	
	public String getCode() {
		return code;
	}
}
