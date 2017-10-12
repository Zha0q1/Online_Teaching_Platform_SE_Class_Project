package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import communication.Update;
import communication.UpdateParser;
import shared.User;

public class ClassroomUserCommunicator extends Thread{
	
	//basic
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private Socket socket;
	private UpdateParser parser;
	private ClassServerListener listener;
	// info
	private User user;
	private OnlineClass onlineClass; // if the user is an instructor
	private int classID = -1;// if the user is a student, default as -1
	
	
	//running?
	private boolean running;
	
	public ClassroomUserCommunicator(Socket s, UpdateParser p, ClassServerListener csl) throws IOException {
		socket = s;
		parser = p;
		listener = csl;
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		running = true;
		start();
	}
	
	public void run(){
		try {
			while (running) {
				Update update = (Update)ois.readObject();
				System.out.println("Read");
				parser.parse(update,this);
			}
		} catch (IOException ioe) {
			listener.removeCommunicator(this);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();

		} finally {
			try {
				if (socket != null) {
					socket.close();
				}
			} catch (IOException ioe) {}
		}
	};
	
	public int getUserID() {
		if (user!= null) {
			return user.getID();
		}
		return -1;
	} 
	
	public User getUser(){
		return user;
	}
	
	public String getUsername() {
		if (user!= null) {
			return user.getUsername();
		}
		return "a guest";
	}
	
	// if the this communicator is "instructing some class"
	public void setOnlineClass(OnlineClass oc) {
		onlineClass = oc;
	}
	
	public OnlineClass getOnlineClass() {
		return onlineClass;
	}
	
	public void setClassID(int classID){
		this.classID = classID;
	}
	
	public int getClassID(){ 
		return classID;
	}
	
	
	public void sendUpdate(Update u) {
		try {
			oos.writeObject(u);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setUser(User usr) {
		this.user = usr;
	}
	

}
