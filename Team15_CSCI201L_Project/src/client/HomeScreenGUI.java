package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import communication.ActivityListRequest;
import communication.ActivityListUpdate;
import communication.CreateNewActivityRequest;
import communication.CreateNewActivityResponse;
import communication.CreateNewClassRequest;
import communication.CreateNewClassResponse;
import communication.CurrentlyAvailableClassesRequest;
import communication.CurrentlyAvailableClassesRequest2;
import communication.DeleteActivityRequest;
import communication.JoinClassRequest;
import communication.OnlineClassListUpdate;
import communication.OnlineClassListUpdate2;
import communication.StartClassRequest;
import communication.StartClassResponse;
import communication.Update;
import shared.Activity;
import shared.Classroom;
import shared.User;
import util.AppearanceSettings;
import util.SwingUtils;
import util.SwingUtils2;
import util.SwingUtils3;

public class HomeScreenGUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JButton hostClass;
	private JButton	joinClass;
	private JButton createActivities;
	private JButton yourClasses;
	private User userInformation;
	private boolean isGuest;
	private JLabel titleOfMainPanel;
	private JTree availableClasses;
	private JTree savedClasses;
	private JTree activities;
	private Map<Integer, String> onlineClasses;
	public Map<Integer, String> userActivityMap;
	private Classroom classroomTesting;
	private JPanel recentlyViewed;
	private DefaultListModel<String> activitiesListDefaultModel;
	private DefaultListModel<String> classesList;
	private DefaultListModel<String> actvityList;
	private JList ListOfClasses ;
	private JList listOfActivites;
	private JPanel leftPanelForActivitiesAndClasses;
	private JPanel activitiesPanel;
	private Vector<Integer> vectorToStoreIDs = new Vector<Integer>();
	public Vector<Integer> vectorToStoreActivityIDs = new Vector<Integer>();
	private JButton deleteActivity;
	private JButton editActivity;
	private JButton refreshOnlineClassesButton;
	
	HomeScreenGUI(User userInformation){
		super("Home Screen GUI");
		this.userInformation = userInformation;
		initializeComponents();
		createGUI();
		
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// TESTING:
		System.out.println("My username is " + userInformation.getUsername());
		System.out.println("My userID is " + userInformation.getID());
		
		CurrentlyAvailableClassesRequest request = new CurrentlyAvailableClassesRequest(userInformation);
		ActivityListRequest requestActivities = new ActivityListRequest(userInformation.getID());
		
		// add this pointer to the communicator
		UserClassroomCommunicator.getInstance().homeScreenGUI = this;
		
		UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateRecieved(Update update) {
								
				if (update instanceof OnlineClassListUpdate) {
					
					OnlineClassListUpdate response = (OnlineClassListUpdate) update;
					
					// process response
					onlineClasses = response.getOnlineClassList();
					remove = true;
					if(onlineClasses.size()!= 0){
						for (Map.Entry<Integer, String> mapEntry : onlineClasses.entrySet()) {
						    // key = e.getKey();
						    String value = mapEntry.getValue();
						    System.out.println("online class " + value);
						    classesList.addElement(value);
						    vectorToStoreIDs.add(mapEntry.getKey());
						}
						ListOfClasses = new JList(classesList);
						
						AppearanceSettings.setBackground(Constants.maroon, recentlyViewed);
						
						
						AppearanceSettings.setForeground(Constants.gold, listOfActivites, ListOfClasses);
						AppearanceSettings.setBackground(Constants.maroon, listOfActivites, ListOfClasses );
						
						//recentlyViewed.setBorder(BorderFactory.createMatteBorder(
				         //       3, 3, 3, 3, Constants.gold));
						recentlyViewed.add(ListOfClasses, BorderLayout.CENTER);
						
					}
				}
			}
		});
		UserClassroomCommunicator.getInstance().sendUpdate(request);
		System.out.println("after reading the online classes ");
		
		UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
			@Override
			public void updateRecieved(Update update) {
								
				if (update instanceof ActivityListUpdate) {
					
					ActivityListUpdate response = (ActivityListUpdate) update;
					
					// process response
					userActivityMap = response.getActivityList();
					remove = true;
					
					if(userActivityMap.size()!= 0){
						for (Map.Entry<Integer, String> mapEntry : userActivityMap.entrySet()) {
						    // key = e.getKey();
						    String value = mapEntry.getValue();
						    
						    System.out.println("activity id " + value);
						    activitiesListDefaultModel.addElement(value);
						    vectorToStoreActivityIDs.add(mapEntry.getKey());
						    
						}
						titleOfMainPanel.setText("Your Activities");
						listOfActivites = new JList(activitiesListDefaultModel);
						JScrollPane scroll = new JScrollPane();
						scroll.setViewportView(listOfActivites);
						activitiesPanel.add(scroll, BorderLayout.CENTER);
						//AppearanceSettings.setBackground(Constants.maroon, activitiesPanel);
												
						AppearanceSettings.setForeground(Constants.gold, listOfActivites, ListOfClasses);
						AppearanceSettings.setBackground(Constants.maroon, listOfActivites, ListOfClasses );
						AppearanceSettings.setFont(Constants.listFont, listOfActivites,ListOfClasses);
						
						
					}
				}
			}
		});
		UserClassroomCommunicator.getInstance().sendUpdate(requestActivities);
		addActionListeners();
	}
	
	private void initializeComponents(){
		hostClass = new JButton("Host new Class");
		joinClass = new JButton("Join a class");
		createActivities = new JButton("Create Activity");
		yourClasses = new JButton("Your classes");
		editActivity = new JButton("Edit activity");
		deleteActivity = new JButton("Delete Activity");
		AppearanceSettings.setSize(100,60,hostClass,joinClass,createActivities,yourClasses);
		AppearanceSettings.setSize(50,20,editActivity,deleteActivity);
		AppearanceSettings.setBackground(Constants.gold, hostClass,joinClass,createActivities,yourClasses,editActivity,deleteActivity);
		AppearanceSettings.setForeground(Constants.maroon, hostClass,joinClass,createActivities,yourClasses,editActivity,deleteActivity);
		hostClass.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Constants.maroon));
		
		joinClass.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Constants.maroon));
		
		createActivities.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Constants.maroon));
		
		yourClasses.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Constants.maroon));
		
		
		if(userInformation.isGuest()){ //can only join a class as a student 
			hostClass.setEnabled(false);
			createActivities.setEnabled(false);
			yourClasses.setEnabled(false);
		}
		
		onlineClasses = new HashMap<Integer, String>(); 
		recentlyViewed = new JPanel(new BorderLayout());
		activitiesPanel = new JPanel(new BorderLayout());
		activitiesListDefaultModel = new DefaultListModel<String>();
		classesList = new DefaultListModel<String>();
		ListOfClasses = new JList<DefaultListModel<String>>();
		listOfActivites = new JList<DefaultListModel<String>>();
		refreshOnlineClassesButton = new JButton("Refresh");
		AppearanceSettings.setBackground(Color.lightGray, refreshOnlineClassesButton);
		refreshOnlineClassesButton.setContentAreaFilled(false);
        refreshOnlineClassesButton.setOpaque(true);
        
        AppearanceSettings.setBackground(Constants.lightPink, editActivity);
		editActivity.setContentAreaFilled(false);
        editActivity.setOpaque(true);
        
        AppearanceSettings.setBackground(Constants.lightPink, deleteActivity);
		deleteActivity.setContentAreaFilled(false);
        deleteActivity.setOpaque(true);
        
        
        refreshOnlineClassesButton.setBorder(BorderFactory.createMatteBorder(
                1, 1, 1, 1, Constants.maroon));
        
        
	}
	
    public ImageIcon scaleImage(ImageIcon icon, int w, int h)
    {
        int nw = icon.getIconWidth();
        int nh = icon.getIconHeight();
        if(icon.getIconWidth() > w)
        {
          nw = w;
          nh = (nw * icon.getIconHeight()) / icon.getIconWidth();
        }
        if(nh > h)
        {
          nh = h;
          nw = (icon.getIconWidth() * nh) / icon.getIconHeight();
        }
        return new ImageIcon(icon.getImage().getScaledInstance(nw, nh, Image.SCALE_DEFAULT));
    }

	private void createGUI(){
		setBackground(Color.red);
		JPanel mainPanel = new JPanel(new GridLayout(1,3));
		
		JPanel panelForImage = new JPanel(new BorderLayout());		
		
		JLabel label = new JLabel();
		Image image = new ImageIcon("resources/projectImage.jpg").getImage();
		ImageIcon a = new ImageIcon(image);
		label.setIcon(a);
		
		JPanel panel = new JPanel(new BorderLayout());
		//AppearanceSettings.setSize(panelForImage.getWidth(), panelForImage.getHeight(), image);
		//JPanel mainPanel = new JPanel(new GridLayout(1, 2));
		
		//JLabel label = new JLabel("", image, JLabel.CENTER);
		//JPanel panel = new JPanel(new BorderLayout());
		this.addComponentListener(new ComponentListener() {
			@Override
			public void componentResized(ComponentEvent e) {
				label.setIcon(scaleImage(a,label.getWidth(),label.getHeight()));
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});	
		panelForImage.setBackground(new Color(172, 37, 41));
		panelForImage.add( label, BorderLayout.CENTER );
		mainPanel.setBackground(Color.red);
		leftPanelForActivitiesAndClasses = new JPanel(new GridLayout(2,1));
		leftPanelForActivitiesAndClasses.add(createRecentlyViewedClasses());
		leftPanelForActivitiesAndClasses.add(createRecentlyViewedActivities());
		leftPanelForActivitiesAndClasses.setBackground(Constants.gold);
		JLabel welcomeLabel = new JLabel("Welcome!");
		panelForImage.setBorder(BorderFactory.createMatteBorder(
                3,3, 3, 3, Constants.gold));
		mainPanel.add(leftPanelForActivitiesAndClasses);
		mainPanel.add(createButtonPanel());
		mainPanel.add(panelForImage);
		
		//add(welcomeLabel, BorderLayout.NORTH);
		add(mainPanel, BorderLayout.CENTER); 
		setSize(1200, 600);
		setVisible(true);
	}
	
	private void addActionListeners(){
		
		final JFrame thisFrame = this;
		
		joinClass.addActionListener(new ActionListener(){
			@SuppressWarnings("unchecked")
			public void actionPerformed(ActionEvent e) {
				int indexSelected = ListOfClasses.getSelectedIndex();
				if(indexSelected != -1){
					joinClassroom(vectorToStoreIDs.get(indexSelected));	
					UserClassroomCommunicator.getInstance().sendUpdate(new JoinClassRequest(vectorToStoreIDs.get(indexSelected)));
				}
					
			}	
		});
		
		yourClasses.addActionListener(new ActionListener() {
	
			public void actionPerformed(ActionEvent e) {
				//request for saved classes
				
				HostClassroomDialog dialog = new HostClassroomDialog(thisFrame, userInformation);
				SwingUtils3.fadeIn(dialog);
				int classroomID = dialog.getSelectedClassroomID();
				
				if (classroomID != -1) { // the user selected a classroom, pressed 'Okay'
					startClassroom(classroomID);
				}
			}
		});
		
		createActivities.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				CreateActivityDialog createActivity = new CreateActivityDialog(thisFrame);
				SwingUtils2.fadeIn(createActivity);
				if (createActivity.success) {
					
					Activity createNewActivity = new Activity(createActivity.getSkeleton(), userInformation, createActivity.getInput(), createActivity.getOutput(), createActivity.getActivityName(), createActivity.getMode());
					
					CreateNewActivityRequest newActivityRequest = new CreateNewActivityRequest(createNewActivity, userInformation);
					UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
						@SuppressWarnings("unchecked")
						@Override
						public void updateRecieved(Update update) {
							
							 if(update instanceof CreateNewActivityResponse){
								CreateNewActivityResponse response = (CreateNewActivityResponse) update;
								userActivityMap.put(response.createdActivity.getID(), response.createdActivity.getName());
								if(userActivityMap.size()!= 0){
									activitiesListDefaultModel = new DefaultListModel();
									for (Map.Entry<Integer, String> mapEntry : userActivityMap.entrySet()) {
									    // key = e.getKey();
									    String value = mapEntry.getValue();
									    System.out.println("activity id " + value);
									    activitiesListDefaultModel.addElement(value);
									    vectorToStoreActivityIDs.add(mapEntry.getKey());
									    
									}
									titleOfMainPanel.setText("Your Activities");
									listOfActivites.setModel(activitiesListDefaultModel);
									//activitiesPanel.add(listOfActivites);
									
								}
								remove = true;
							}
						}
					});
					UserClassroomCommunicator.getInstance().sendUpdate(newActivityRequest);
				}
			}
		});
		
		
		hostClass.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				HostClassDialog dialog = new HostClassDialog(thisFrame, userActivityMap);
				SwingUtils.fadeIn(dialog);
				if (dialog.success) {
					
					Classroom classroom = new Classroom(userInformation, dialog.getClassName(), null);
					
					CreateNewClassRequest newClassRequest = new CreateNewClassRequest(classroom, dialog.selectedActivity);
					UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
						@Override
						public void updateRecieved(Update update) {
							
							 if(update instanceof CreateNewClassResponse){
								CreateNewClassResponse response = (CreateNewClassResponse) update;
								
								startClassroom(response.classroomInstance.getID());
								
								remove = true;
							}
						}
					});
					UserClassroomCommunicator.getInstance().sendUpdate(newClassRequest);
				}
			}
		});
		
		deleteActivity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int indexSelected = listOfActivites.getSelectedIndex();
				if(indexSelected != -1)
					new DeleteActivityRequest(userInformation, vectorToStoreActivityIDs.get(indexSelected));
			}
		});
		
		refreshOnlineClassesButton.addActionListener(new ActionListener() {
			
			
			public void actionPerformed(ActionEvent e) {
				//System.out.println("in refresh action performed");
				CurrentlyAvailableClassesRequest2 requestForRefreshedClasses = new CurrentlyAvailableClassesRequest2();
				
				//UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
//					@Override
//					public void updateRecieved(Update update) {
//						System.out.println("in update received");
//						if (update instanceof OnlineClassListUpdate2) {
//							System.out.println("inside reponse if");
//							OnlineClassListUpdate2 responseRefresh = (OnlineClassListUpdate2) update;
//							
//							System.out.println("sizee "+ responseRefresh.getOnlineClassList().size());
//							
//							// process response
//							onlineClasses = responseRefresh.getOnlineClassList();
//							remove = true;
//							if(onlineClasses.size()!= 0){
//								for (Map.Entry<Integer, String> mapEntry : onlineClasses.entrySet()) {
//								    // key = e.getKey();
//								    String value = mapEntry.getValue();
//								    System.out.println("online class " + value);
//								    classesList.addElement(value);
//								    vectorToStoreIDs.add(mapEntry.getKey());
//								}
//								System.out.println("number of classes online " + classesList.size());
//								ListOfClasses = new JList(classesList);
//								recentlyViewed = new JPanel();
//								recentlyViewed.add(ListOfClasses);
//							}
						//}
					//}
			//	});
				
				UserClassroomCommunicator.getInstance().sendUpdate(requestForRefreshedClasses);
			}
		});
	}
	
	public void refreshClassList(DefaultListModel model, Vector<Integer> vector) {
		System.out.println("yyyera");
		classesList = model;
		vectorToStoreIDs = vector;
		System.out.println("number of classes online " + classesList.size());
		ListOfClasses.setModel(classesList);
	}
	
	private JPanel createRecentlyViewedClasses(){  //request server for information about the recently viewed classes
		
		titleOfMainPanel = new JLabel("Currently online classes");
	
		JPanel northPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		AppearanceSettings.setFont(Constants.headerFont, titleOfMainPanel);
		AppearanceSettings.setBackground(Constants.maroon, buttonPanel);
		AppearanceSettings.setBackground(Constants.gold, northPanel);
		AppearanceSettings.setForeground(Constants.gold, northPanel, buttonPanel);
		AppearanceSettings.setForeground(Constants.maroon, refreshOnlineClassesButton);
		northPanel.add(titleOfMainPanel, BorderLayout.NORTH);
		
		AppearanceSettings.setTextAlignment(titleOfMainPanel);
		buttonPanel.add(refreshOnlineClassesButton);
		
		recentlyViewed.add(northPanel, BorderLayout.NORTH);
		recentlyViewed.add(buttonPanel, BorderLayout.SOUTH);
		AppearanceSettings.setBackground(Constants.maroon, recentlyViewed);
		recentlyViewed.setBorder(BorderFactory.createMatteBorder(
                3, 3, 3, 3, Constants.gold));
		return recentlyViewed;
	}
	
	private JPanel createRecentlyViewedActivities(){  //request server for information about the recently viewed classes
		JPanel northPanel = new JPanel();
		JPanel buttonPanel = new JPanel(new GridLayout(1,2));
		AppearanceSettings.setBackground(Constants.gold, northPanel);
		AppearanceSettings.setBackground(Constants.maroon, buttonPanel);
		AppearanceSettings.setForeground(Constants.gold, northPanel, buttonPanel, editActivity, deleteActivity);
		AppearanceSettings.setBackground(Constants.lightPink, editActivity, deleteActivity);

		titleOfMainPanel = new JLabel("Your Activities");
		AppearanceSettings.setFont(Constants.headerFont, titleOfMainPanel);
		AppearanceSettings.setTextAlignment(titleOfMainPanel);
		northPanel.add(titleOfMainPanel);
		//buttonPanel.add(editActivity);
		//buttonPanel.add(deleteActivity);
		
		activitiesPanel.add(northPanel, BorderLayout.NORTH);
		//activitiesPanel.add(buttonPanel, BorderLayout.SOUTH);
		AppearanceSettings.setBackground(Constants.maroon, activitiesPanel);
		activitiesPanel.setBorder(BorderFactory.createMatteBorder(
                3, 3, 3, 3, Constants.gold));
		return activitiesPanel;
	}
	
	private JPanel createButtonPanel(){
//		JPanel threeVerticalCols = new JPanel(new GridLayout(1,3));
//		JPanel buttonPanel = new JPanel(new GridLayout(8,1));
//
//		JPanel emptyPanel = new JPanel();
//		JPanel biggerEmptyPanel = new JPanel();
//		AppearanceSettings.setBackground(Constants.maroon, biggerEmptyPanel);
//		AppearanceSettings.setBackground(Constants.maroon, emptyPanel);
//		AppearanceSettings.setBackground(Constants.maroon, buttonPanel);
//		AppearanceSettings.setBackground(Constants.maroon, threeVerticalCols);
//		buttonPanel.add(hostClass);
//		buttonPanel.add(new JPanel());
//		//buttonPanel.add(new JPanel());
//		buttonPanel.add(joinClass);
//		buttonPanel.add(emptyPanel);
//		//buttonPanel.add(new JPanel());
//		buttonPanel.add(yourClasses);
//		buttonPanel.add(emptyPanel);
//		//buttonPanel.add(new JPanel());
//		buttonPanel.add(createActivities);
//		buttonPanel.add(emptyPanel);
//		//buttonPanel.add(new JPanel());
//
//		//threeVerticalCols.add(biggerEmptyPanel);
//		threeVerticalCols.add(biggerEmptyPanel);
//		threeVerticalCols.add(buttonPanel);
//		threeVerticalCols.add(biggerEmptyPanel);
		//threeVerticalCols.add(biggerEmptyPanel);
		//return buttonPanel;
		
//		AppearanceSettings.setBackground(Constants.maroon, emptyPanel);
//		AppearanceSettings.setBackground(Constants.maroon, buttonPanel);
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx=30;
		gbc.gridx=0;
		gbc.gridy =0;
		buttonPanel.add(hostClass, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx=30;
		gbc.gridx=0;
		gbc.gridy =1;
		gbc.insets = new Insets(40,0,0,0);
		buttonPanel.add(joinClass, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx=30;
		gbc.gridx=0;
		gbc.gridy =2;
		gbc.insets = new Insets(40,0,0,0);
		buttonPanel.add(yourClasses, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipadx=30;
		gbc.gridx=0;
		gbc.gridy =3;
		gbc.insets = new Insets(40,0,0,0);
		buttonPanel.add(createActivities, gbc);
		AppearanceSettings.setBackground(Constants.maroon, buttonPanel);
		AppearanceSettings.setForeground(Constants.maroon, hostClass,joinClass,yourClasses,createActivities);
		buttonPanel.setBorder(BorderFactory.createMatteBorder(
                3, 3, 3, 3, Constants.gold));
		return buttonPanel;
	}
	

	
	public void joinClassroom(int ID) {
		
		ClassroomGUI classroomGUI = new ClassroomGUI(userInformation, ID);
		classroomGUI.setVisible(true);
		
		this.setVisible(false);
		this.dispose();
	}
		
	public void startClassroom(int ID) {
		
		StartClassRequest request = new StartClassRequest(ID);
		
		UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
			@Override
			public void updateRecieved(Update update) {
				
				 if(update instanceof StartClassResponse){
					StartClassResponse response = (StartClassResponse)update;
					
					joinClassroom(ID);
					
					remove = true;
				}
			}
		});
		UserClassroomCommunicator.getInstance().sendUpdate(request);
	}
}