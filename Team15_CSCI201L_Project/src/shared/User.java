package shared;

import java.io.Serializable;

public class User implements Serializable {
	
	private static final long serialVersionUID = 433649323500762271L;
	
	//
	// Member Variables
	//
	
	private boolean guest = false;
	public boolean isGuest() {
		return guest;
	}
	
	private transient static int mostRecentGuestID = 0;
	private int guestID = -1;
	
	private int userID = -1;
	public int getID() {
		return userID;
	}
	
	private String username;
	public String getUsername() {
		return username;
	}
	
	public String getDisplayName() {
		return guest ? ("Guest #" + guestID) : username;
	}
	
	//
	// Methods:
	//
	
	// Guest User
	public User() {
		guest = true;
		guestID = --mostRecentGuestID;
		userID = mostRecentGuestID;
	}
	
	// Registered User
	public User(int ID, String n) {
		userID = ID;
		username = n;
	}
	
	public boolean equals(Object other) {
		User otherUser = (User)other;
		
		if (otherUser.guest && this.guest) {
			if (otherUser.guestID == this.guestID) return true;
		} else if (!otherUser.guest && !this.guest) {
			if (otherUser.userID == this.userID) return true;
		}
		
		return false;
	}
}
