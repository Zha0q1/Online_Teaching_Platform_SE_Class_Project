package server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

public class ServerGUI extends JFrame{
	
	private static final long serialVersionUID = 1L;
	
	private static final int port = 1121;
	
	private ClassServerListener classServerListener;
	
	private JButton startButton;
	private JList<String> classList;
	private DefaultListModel<String> model;
	
	public ServerGUI() {
		super("Server");
		initializeComponents();
		createGUI();
		addEvents();
		setVisible(true);
	}
	
	public void initializeComponents() {
		model = new DefaultListModel<String>();
		classList = new JList<String>(model);
		startButton = new JButton("Start Server");
	}
	

	public void createGUI() {
		setSize(400,600);
		setResizable(false);
		
		add(new JScrollPane(classList), BorderLayout.CENTER);
		add(startButton, BorderLayout.SOUTH);
	}
	
	public void addEvents() {
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent we) {
				  System.exit(0);
			  }
		});
		
		startButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if (startButton.getText().equals("Shutdown Server and Exit")){ 
					 System.exit(0);
				}
				try {
					classServerListener = new ClassServerListener(port);
				} catch (IOException e1) {
					System.out.println("error starting server");
				}
				startButton.setText("Shutdown Server and Exit");
				new RefreshList();
			}
		});
	}
	
	public void populateList(long time) {
		model = new DefaultListModel(); 
		model.addElement("Server online for "+ time + "s. " + classServerListener.getNumLogedinUsers() +  " users logeg in; " + classServerListener.getNumGuests() + " guests.");
		model.addElement("The classes online are: ");
		Map<Integer, String> classes = classServerListener.getOnlineClassList();
		for (Entry<Integer, String> entry : classes.entrySet()){
			model.addElement(entry.getValue());
		}
		classList.setModel(model);
	}
	
	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		new ServerGUI();
	}
	
	private class RefreshList extends Thread {
		
		private long time;
		
		public RefreshList() {
			time = 0;
			this.start();
		}
		
		public void run() {
			try {
				while (true) {
					RefreshList.sleep(1000);
					time ++;
					populateList(time);
				}
			} catch (InterruptedException e) {
			}	
		}	
	}
}
