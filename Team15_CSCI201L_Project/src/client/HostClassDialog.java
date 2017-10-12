package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import listeners.TextFocusListener;
import shared.Classroom;
import shared.User;
import util.AppearanceSettings;
import util.SwingUtils;

public class HostClassDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private Classroom classroom;
	private JTextField className;
	private JButton startClass, cancel;
	private JPanel centerPanel, buttonPanel;
	private Boolean activityChosen = false;
	private User userinfo;
	public Vector<Integer> vectorToStoreActivityIDs = new Vector<Integer>();
	public Map<Integer, String> userActivityMap;
	private JComboBox selectActivity = new JComboBox();
	public int selectedActivity;
	
	JList<String> list;
	DefaultListModel<String> listModel;
	
	private int[] activities;
	
	public boolean success = false;
	
	public HostClassDialog(JFrame frame,Map<Integer, String> userActivityMap){
		super(frame, "Host a new class", true);
		//this.vectorToStoreActivityIDs = vectorToStoreActivityIDs;
		this.userActivityMap = userActivityMap;
		initializeVariables();
		createGUI();
		setUndecorated(true);
		this.getRootPane().setOpaque(false);
		SwingUtils.createDialogBackPanel(this,  frame.getContentPane());
		addEvents();
	}
	@SuppressWarnings("unchecked")
	public void initializeVariables(){
		className = new JTextField(15);
		startClass = new JButton("Host Class");
		cancel = new JButton("Cancel");
		startClass.setEnabled(false);
		for (Map.Entry<Integer, String> mapEntry : userActivityMap.entrySet()) {
		    String value = mapEntry.getValue();
		    System.out.println("activity id " + value);
		    selectActivity.addItem(value);
		    		    
		    vectorToStoreActivityIDs.add(mapEntry.getKey());   
		}
	}
	public void createGUI(){
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new BoxLayout(northPanel,BoxLayout.Y_AXIS));
		centerPanel = new JPanel();
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1,2));
		JLabel classAndActivityLabel = new JLabel("Name this class and choose an activity");
		northPanel.add(classAndActivityLabel);
		northPanel.add(className);
		//Setting Background and foreground and text;
		AppearanceSettings.setBackground(Constants.maroon, classAndActivityLabel, className, selectActivity, cancel, startClass);
		AppearanceSettings.setBackground(Constants.maroon, northPanel,centerPanel,buttonPanel);
		AppearanceSettings.setForeground(Constants.gold, classAndActivityLabel, className, selectActivity, cancel, startClass);
		AppearanceSettings.setForeground(Constants.gold, northPanel,centerPanel,buttonPanel);
		AppearanceSettings.setFont(Constants.standardFont, classAndActivityLabel, className, cancel, startClass);
		
		AppearanceSettings.setTextAlignment(classAndActivityLabel);
				
		buttonPanel.add(cancel);
		buttonPanel.add(startClass);
		add(northPanel,BorderLayout.NORTH);
		add(centerPanel,BorderLayout.CENTER);
		add(buttonPanel,BorderLayout.SOUTH);
		centerPanel.add(selectActivity);
	}
	public void addEvents(){
		//setSize(400,300);
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		className.addFocusListener(new TextFocusListener("Name this class", className));
		className.getDocument().addDocumentListener(new MyDocumentListener());
		startClass.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				//null should be changed to the activity chosen from the combobox
				int index = selectActivity.getSelectedIndex();
								
				if(index != -1)
					selectedActivity = vectorToStoreActivityIDs.get(index);
				success = true;
				SwingUtils.fadeOut(HostClassDialog.this);
				
			}
		});
		cancel.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				setVisible(false);
//				dispose();
				SwingUtils.fadeOut(HostClassDialog.this);
			}
		});
//		listModel= new DefaultListModel<String>();
//		list = new JList<String>(listModel);
//		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		list.setEnabled(false);
//		
//		list.addListSelectionListener(new ListSelectionListener(){
//			@Override
//			public void valueChanged(ListSelectionEvent e) {
//				// TODO Auto-generated method stub
//				startClass.setEnabled(list.getSelectedIndex()!=-1 && canPressButton());
//			}
//			
//		});
		
		
		
//		centerPanel.add(list);

	}
	public String getClassName(){
		return className.getText();
	}
	public Boolean canPressButton(){
		return (!className.getText().isEmpty() && !className.getText().equals("Name this class"));
	}
	//sets the buttons enabled or disabled
		private class MyDocumentListener implements DocumentListener{
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				startClass.setEnabled(canPressButton());
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				startClass.setEnabled(canPressButton());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				startClass.setEnabled(canPressButton());
			}
		}
}
