package client;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.GroupLayout;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import communication.ClassroomInitializationPackage;
import communication.ClassroomInitializationPackageRequest;
import communication.InstructorLeaveRequest;
import communication.StartableClassListRequest;
import communication.StartableClassListUpdate;
import communication.StudentLeaveRequest;
import communication.Update;
import shared.Classroom;
import shared.ClassroomFeature;
import shared.User;
import util.AppearanceSettings;

public class ClassroomGUI extends JFrame {

	private static final long serialVersionUID = -4491869616102286067L;
	
	//
	// Static Variables:
	//
	
	private static final int SIDEBAR_WIDTH = 400;
	private static final int INFO_PANEL_MINIMUM_HEIGHT = 100;
	private static final int INFO_PANEL_PREFERRED_HEIGHT = 300;
	
	//
	// Member Variables:
	//

	private ClassroomInfoPanel infoPanel;
	
	private User user;
	private Classroom classroom;
	
	private JMenuItem leaveButton;
	public TabPanel tabPanel;
	
	public boolean isInstructor() {
		return classroom.isInstructor(user);
	}
	
	//
	// Methods:
	//
	
	public ClassroomGUI(User user, int classroomID)
	{
		super("Loading...");
		
		this.user = user;
		
		UserClassroomCommunicator.getInstance().classroomGUI = this;
		
		loadClassroom(classroomID);
		
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		//setSize(1200, 900);
		//createGUI();
		//addActionListeners();
	}
	
	private void loadClassroom(int ID) {
		
		ClassroomInitializationPackageRequest request = new ClassroomInitializationPackageRequest(ID);
		
		UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
			@Override
			public void updateRecieved(Update update) {
								
				if (update instanceof ClassroomInitializationPackage) {
					
					ClassroomInitializationPackage response = (ClassroomInitializationPackage)update;
					
					classroom = response.getClassroom();
					System.out.println(classroom.getActivity().getSkeletonCode());
					UserClassroomCommunicator.getInstance().classroom = classroom;
					
					//System.out.println("ClassroomGUI -- " + classroom.getWhiteboard().lines.size());
					
					
					setTitle(classroom.getName());
					createGUI();
					addActionListeners();
					
					remove = true;
				}
			}
		});
					
		UserClassroomCommunicator.getInstance().sendUpdate(request);

		
	}
	
	private void createGUI() {
		
		JMenuBar menuBar = new JMenuBar();
			
			JMenu applicationMenu = new JMenu("Application");
			applicationMenu.setFont(Constants.standardFont);
			
				leaveButton = new JMenuItem("Leave Classroom");
				leaveButton.setFont(Constants.standardFont);
				applicationMenu.add(leaveButton);
			
			menuBar.add(applicationMenu);
		
		setJMenuBar(menuBar);
		
		setMinimumSize(new Dimension(SIDEBAR_WIDTH * 3, INFO_PANEL_PREFERRED_HEIGHT * 2));
		
		GroupLayout layout = new GroupLayout(getContentPane());
		setLayout(layout);
		
		tabPanel = new TabPanel(classroom.isInstructor(user));
		
		for (ClassroomFeature<?, ?> feature : classroom.getFeatures())
		{	
			ClassroomFeaturePanel<?> panel = feature.getPanel(classroom, user);
			if (panel.isTab()) tabPanel.addTab(panel.getName(), panel);
		}

		infoPanel = new ClassroomInfoPanel(classroom, user);
		
		JPanel chatboxPanel = classroom.getChatbox().getPanel(classroom, user);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		layout.setHorizontalGroup(
			layout.createSequentialGroup()
				.addComponent(tabPanel)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(infoPanel, SIDEBAR_WIDTH, SIDEBAR_WIDTH, SIDEBAR_WIDTH)
						.addComponent(chatboxPanel, SIDEBAR_WIDTH, SIDEBAR_WIDTH, SIDEBAR_WIDTH)
				)
		);
		
		layout.setVerticalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(tabPanel)
				.addGroup(layout.createSequentialGroup()
						.addComponent(infoPanel, INFO_PANEL_MINIMUM_HEIGHT, INFO_PANEL_PREFERRED_HEIGHT, INFO_PANEL_PREFERRED_HEIGHT)
						.addComponent(chatboxPanel, 0, INFO_PANEL_PREFERRED_HEIGHT * 2, Short.MAX_VALUE)
				)
		);
		
		AppearanceSettings.setForeground(Constants.gold, menuBar, applicationMenu, leaveButton);
		
		this.getContentPane().setBackground(Constants.gold);
		AppearanceSettings.setBackground(Constants.maroon, menuBar, applicationMenu,leaveButton);
		AppearanceSettings.setBackground(Constants.maroon, tabPanel, infoPanel, chatboxPanel);
		
	}
	
	//
	// Testing:
	//
	public void addActionListeners(){
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			  public void windowClosing(WindowEvent e) {
				  System.exit(0);
				  dispose();
			  }
			});
		leaveButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leaveMessage();
			}
		});
	}
	
	public void InstructorLeft() {
		int confirmed = JOptionPane.showConfirmDialog(null, 
		        "The instructor left the classroom", "Instructor Left",
		        JOptionPane.PLAIN_MESSAGE);
		new HomeScreenGUI(user).setVisible(true);
		dispose(); 
	}
	
	public void leaveMessage(){
		if (classroom.isInstructor(user)){ 
			  int confirmed = JOptionPane.showConfirmDialog(null, 
				        "Would you like to save the state of the class?", "Exiting class...",
				        JOptionPane.YES_NO_OPTION);

				    if (confirmed == JOptionPane.YES_OPTION) {
				    	//save state of the class
				    }
			    	UserClassroomCommunicator.getInstance().sendUpdate(new InstructorLeaveRequest(true));
				    new HomeScreenGUI(user).setVisible(true);
			    	dispose(); 
	      }else{
	    	  int confirmed = JOptionPane.showConfirmDialog(null, 
				        "Do you want to stay in the class?", "Stay?",
				        JOptionPane.YES_NO_OPTION);

				    if (confirmed == JOptionPane.YES_OPTION) {
				    	
				    	  
				    }else{
				    	UserClassroomCommunicator.getInstance().sendUpdate(new StudentLeaveRequest());
				    	new HomeScreenGUI(user).setVisible(true);
				    	 dispose(); 
				    }
	      }
	}	

	public void updateStudentList() {
		infoPanel.classroomInfoUpdated();
	}

}
