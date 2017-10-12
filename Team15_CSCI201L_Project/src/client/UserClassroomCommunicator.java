package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import communication.Update;
import communication.UpdateParser;
import shared.Classroom;

public class UserClassroomCommunicator extends Thread{

	private static String HOST = "localhost";
	private static final int PORT = 1121;
	
	//
	// Singleton Behavior:
	//
	
	private static UserClassroomCommunicator instance = null;
	
	public static boolean setServerAddress(String address) {
		HOST = address;
		
		try {
			getInstanceOrFail();
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private static UserClassroomCommunicator getInstanceOrFail() throws IOException {
		if (instance == null) {
			instance = new UserClassroomCommunicator(HOST, PORT);
		}
		return instance;
	}
	
	public static UserClassroomCommunicator getInstance() {
		try {
			return getInstanceOrFail();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//
	// Member Variables:
	//
	
	//basic
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	private UpdateParser parser;
	//running?
	private boolean running;
	
	public Classroom classroom;
	public ClassroomGUI classroomGUI;
	public HomeScreenGUI homeScreenGUI;
	//
	// Methods:
	//
	
	private UserClassroomCommunicator(String host, int port) throws IOException {
		socket = new Socket(host, port);
		ois = new ObjectInputStream(socket.getInputStream());
		oos = new ObjectOutputStream(socket.getOutputStream());
		running  = true;
		parser = new UpdateParser(null);
		this.start();
	}
	
	public void run() {
		try {
			while (running) {
				Update update = (Update)ois.readObject();
				
				int count = updateListeners.size();
				for (int i=0; i<count; i++) {
					updateListeners.get(i).updateRecieved(update);
				}
								
				for (int i=0; i<updateListeners.size(); i++) {
					if (updateListeners.get(i).remove) updateListeners.remove(i--);
				}				
								
				parser.parse(update,this);
			}
		} catch (IOException ioe) {
			int confirmed = JOptionPane.showConfirmDialog(null, 
			        "The server is down!", "Connection Lost",
			        JOptionPane.PLAIN_MESSAGE);
			System.exit(0);
		} catch (ClassNotFoundException e) {
			//TODO
		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException ioe) {}
			
			if (parser != null) parser.close();
		}
	}
	
	public void sendUpdate(Update u) {
		try {
			oos.writeObject(u);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Update Listener System:
	
	private ArrayList<UpdateListener> updateListeners = new ArrayList<UpdateListener>();
	
	public static abstract class UpdateListener {
		public boolean remove = false;
		abstract public void updateRecieved(Update update);
	}
	public void addUpdateListener(UpdateListener listener) {
		updateListeners.add(listener);
	}
	public void removeUpdateListener(UpdateListener listener) {
		updateListeners.remove(listener);
	}
	
	public static void main(String args[]){
		try {
			new UserClassroomCommunicator("localhost",1121);
			new UserClassroomCommunicator("localhost",1121);
			while(true){}
		} catch (IOException e) {
			System.out.println("client ");
		}
	}
	
}
