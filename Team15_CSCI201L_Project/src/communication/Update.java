package communication;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public abstract class Update implements Serializable {
	//
	// Member Variables:
	//
	public final Timestamp timeSent = new Timestamp(new Date().getTime());
	protected int sentByID;
	protected boolean sentToAll;
	protected int sentToID;
	public int getSenderID() {
		return sentByID;
	}
	public boolean isSentToAll() {
		return sentToAll;
	}
	public int getReceiverID() {
		return sentToID;
	}
}

