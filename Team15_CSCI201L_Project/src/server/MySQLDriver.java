package server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

import com.mysql.jdbc.Driver;

import shared.Activity;
import shared.Classroom;
import shared.User;

public class MySQLDriver {
	
	//
	// Static: properties file reading
	//
	
	private static final String propertiesFilename = "db.properties";
	private static Properties properties = null;
	
	private static void getProperties() {
		if (properties != null) return;
		
		FileReader reader = null;
		try
		{
			File propertiesFile = new File(propertiesFilename);
			reader = new FileReader(propertiesFile);
			
			properties = new Properties();
			properties.load(reader);
			
		} catch (FileNotFoundException e) {
			System.err.println("Database properties file not found!");
		} catch (IOException e) {
			System.err.println("IOException reading properties file: " + e.getLocalizedMessage());
		} finally {
			try {
				if (reader != null) reader.close();
			} catch (IOException e) {
				System.err.println("IOException closing properties file: " + e.getLocalizedMessage());
			}
		}
	}
	
	private static String dbPort() {
		getProperties();
		
		if (!properties.containsKey("port")) System.err.println("Key 'port' not found in properties file!");
		
		return properties.getProperty("port");
	}
	
	private static String dbSchema() {
		getProperties();
		
		if (!properties.containsKey("schema")) System.err.println("Key 'schema' not found in properties file!");
		
		return properties.getProperty("schema");
	}
	
	private static String dbUsername() {
		getProperties();
		
		if (!properties.containsKey("username")) System.err.println("Key 'username' not found in properties file!");
		
		return properties.getProperty("username");
	}
	
	private static String dbPassword() {
		getProperties();
		
		if (!properties.containsKey("password")) System.err.println("Key 'password' not found in properties file!");
		
		return properties.getProperty("password");
	}
	
	//
	// Member Variables:
	//
	
	private Connection con;
	
	//
	// Database Open/Close Methods:
	//
	
	public MySQLDriver() {
		try {
			new Driver();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void connect() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:"+dbPort()+"/"+dbSchema()+"?user="+dbUsername()+"&password="+dbPassword()+"&useSSL=false");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void stop() {
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//
	// Data Methods:
	//
	
	// returns a User object for the corresponding user, or NULL if no matching user exists
	//
	// case-insensitive for username, but case-sensitive for password (as dictated by the database schema)
	public User validateUser(String username, String password) {
		String query = "SELECT userID, username FROM users WHERE `username`=? AND `password`=?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ResultSet result = ps.executeQuery();
			
			if (result.next()) return new User(result.getInt(1), result.getString(2));
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	// returns true iff there is a user in the database with the provided username
	public boolean usernameTaken(String username) {
		String query = "SELECT * FROM users WHERE `username`=?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, username);
			ResultSet result = ps.executeQuery();
			if (result.next()) return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	// returns the User object iff the operation succeeds (the username was not taken), otherwise null.
	public User addUser(String username, String password) {
		if (usernameTaken(username)) return null;
		
		String query = "INSERT INTO users (`username`, `password`) VALUES (?, ?)";
				
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setString(1, username);
			ps.setString(2, password);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return validateUser(username, password);
	}

	
	// returns an ArrayList of the classrooms owned by given user, ordered by: more recently joined by that user first
	public ArrayList<Classroom> instructorClassroooms(int userID) {
		String query = "SELECT classroomID, object, MAX(users_classrooms.joined) AS joined FROM classrooms "
						+ "LEFT JOIN users_classrooms USING (classroomID) "
						+ "WHERE "
							+ "classrooms.instructorID = ? "
						        + "AND (users_classrooms.userID = ? OR users_classrooms.userID IS NULL) "
						        + "AND NOT deleted "
							+ "GROUP BY classrooms.classroomID "
						    + "ORDER BY joined DESC";
		
		ArrayList<Classroom> out = new ArrayList<Classroom>();
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, userID);
			ps.setInt(2, userID);
			ResultSet result = ps.executeQuery();
			
			while (result.next()) {
				
				int classroomID = result.getInt(1);
				
				byte[] buf = result.getBytes(2);
				ObjectInputStream objectIn = null;
				if (buf != null)
					objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
				
				Classroom classroom = (Classroom)objectIn.readObject();
				classroom.setID(classroomID);
				out.add(classroom);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		return out;
	}
	
	// Record a joining of a classroom by a user.
	public void joinClassroom(User user, Classroom classroom) {
		String query = "INSERT INTO users_classrooms (userID, classroomID) VALUES (?, ?)";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, user.getID());
			ps.setInt(2, classroom.getID());
					
			ps.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Add the given Classroom object to the database. After execution, the ID field of the passed-in Classroom object will be set!
	public void createClassroom(Classroom classroom) {
		String insertQuery = "INSERT INTO classrooms (instructorID, object) VALUES (?, ?)";
		String selectQuery = "SELECT MAX(classroomID) FROM classrooms";
		
		try {
			
			PreparedStatement insertPS = con.prepareStatement(insertQuery);
			insertPS.setInt(1, classroom.getInstructor().getID());
			insertPS.setObject(2, classroom);
			
			PreparedStatement selectPS = con.prepareStatement(selectQuery);
			
			insertPS.execute("LOCK TABLES classrooms WRITE");
			insertPS.executeUpdate();
			ResultSet result = selectPS.executeQuery();
			insertPS.execute("UNLOCK TABLES");
			
			result.next();
			int ID = result.getInt(1);
			
			classroom.setID(ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	
	// returns null if no classroom with that ID exists
	public Classroom getClassroom(int ID) {
		String query = "SELECT object FROM classrooms WHERE classroomID=?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, ID);
			
			ResultSet result = ps.executeQuery();
			
			if (!result.next()) return null; // The ID is bad
			
			byte[] buf = result.getBytes(1);
			if (buf == null) return null;
			
			ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			
			Classroom classroom = (Classroom)objectIn.readObject();
			classroom.setID(ID);
			
			//System.out.println("MySQLDriver::getClassroom -- " + classroom.getWhiteboard().lines.size());
			
			return classroom;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null; // An exception was thrown!
	}
	
	// Update the given classroom in the database. The Classroom object itself stores the classroomID, so only one parameter is necessary.
	public void updateClassroom(Classroom classroom) {
		
		//System.out.println(classroom.getWhiteboard().lines.size());
		
		String query = "UPDATE classrooms SET object=? WHERE classroomID=?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setObject(1, classroom);
			ps.setInt(2, classroom.getID());
			
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// Create a new activity. After the method executes, the activity object will be updated with the ID.
	public void createActivity(Activity activity, User user) {
		
		String insertQuery = "INSERT INTO activities (instructorID, object) VALUES (?, ?)";
		String selectQuery = "SELECT MAX(activityID) FROM activities";
		
		try {
			
			PreparedStatement insertPS = con.prepareStatement(insertQuery);
			insertPS.setInt(1, user.getID());
			insertPS.setObject(2, activity);
			
			PreparedStatement selectPS = con.prepareStatement(selectQuery);
			
			insertPS.execute("LOCK TABLES activities WRITE");
			insertPS.executeUpdate();
			ResultSet result = selectPS.executeQuery();
			insertPS.execute("UNLOCK TABLES");
			
			result.next();
			int ID = result.getInt(1);
			
			activity.setID(ID);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}		
	}
	
	// Gets all the activities corresponding to a user
	public ArrayList<Activity> instructorActivities(int userID) {
		String query = "SELECT activityID, object FROM activities WHERE instructorID=? AND deleted=FALSE";
		
		ArrayList<Activity> out = new ArrayList<Activity>();
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, userID);
			ResultSet result = ps.executeQuery();
			
			while (result.next()) {
				
				int activityID = result.getInt(1);
				
				byte[] buf = result.getBytes(2);
				ObjectInputStream objectIn = null;
				if (buf != null)
					objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
				
				Activity activity = (Activity)objectIn.readObject();
				activity.setID(activityID);
				out.add(activity);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	
		return out;
	}
	
	// Get the activity with the given ID
	public Activity getActivity(int ID) {
		String query = "SELECT object FROM activities WHERE activityID=?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, ID);
			
			ResultSet result = ps.executeQuery();
			
			if (!result.next()) return null; // The ID is bad
			
			byte[] buf = result.getBytes(1);
			if (buf == null) return null;
			
			ObjectInputStream objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			
			Activity activity = (Activity)objectIn.readObject();
			activity.setID(ID);
			
			//System.out.println("MySQLDriver::getClassroom -- " + classroom.getWhiteboard().lines.size());
			
			return activity;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null; // An exception was thrown!
	}
	
	// Set the deleted field of the activity to TRUE
	public void deleteActivity(int ID) {
		
		String query = "UPDATE classrooms SET deleted=TRUE WHERE activityID=?";
		
		try {
			PreparedStatement ps = con.prepareStatement(query);
			ps.setInt(1, ID);
			
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
//	// Here is an example of how to read/write serialized objects to the database:
//	public static void main(String[] args) {
//
//		class Person implements Serializable {
//		
//			private static final long serialVersionUID = 1L;
//			
//			public String name;
//			public Person(String n) {
//				name = n;
//			}
//		}
//		
//		MySQLDriver mysql = null;
//		try {
//			mysql = new MySQLDriver();
//			mysql.connect();
//			
//			/*
//			Blob jim = new Blob("JIM");
//			
//			String query = "INSERT INTO classrooms (instructorID, object) VALUES (?, ?)";
//			try {
//				PreparedStatement ps = mysql.con.prepareStatement(query);
//				ps.setInt(1, 1);
//				ps.setObject(2, jim);
//				ps.executeUpdate();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//			*/
//		
//			String query = "SELECT object FROM classrooms";
//			try {
//				PreparedStatement ps = mysql.con.prepareStatement(query);
//				ResultSet result = ps.executeQuery();
//								
//				result.next();
//
//				byte[] buf = result.getBytes(1);
//				ObjectInputStream objectIn = null;
//				if (buf != null)
//					objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
//
//				Person out = (Person)objectIn.readObject();
//				System.out.println(out.name);
//				
//			} catch (SQLException | ClassNotFoundException | IOException e) {
//				e.printStackTrace();
//			}
//			
//		} finally {
//			if (mysql != null) mysql.stop();
//		}
//	}
	
}

