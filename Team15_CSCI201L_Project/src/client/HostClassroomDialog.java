package client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import communication.ActivityListUpdate;
import communication.StartableClassListRequest;
import communication.StartableClassListUpdate;
import communication.Update;
import shared.User;
import util.AppearanceSettings;
import util.SwingUtils3;

public class HostClassroomDialog extends JDialog {

	private static final long serialVersionUID = -3135926764530586098L;

	JList<String> list;
	DefaultListModel<String> listModel;
	JButton okayButton;
	JButton cancelButton;
	
	private int[] classroomIDs;
	
	public HostClassroomDialog(JFrame owner, User user) {
		super(owner, "Start a Saved Class", true);
		
		createGUI();
		
		StartableClassListRequest request = new StartableClassListRequest(user);
		UserClassroomCommunicator.getInstance().addUpdateListener(new UserClassroomCommunicator.UpdateListener() {
			@Override
			public void updateRecieved(Update update) {
								
				if (update instanceof StartableClassListUpdate) {
					
					StartableClassListUpdate response = (StartableClassListUpdate) update;
					
					setClassrooms(response.getStartableClassList());
					
					remove = true;
				}
				

			}
		});
		UserClassroomCommunicator.getInstance().sendUpdate(request);
		setUndecorated(true);
		this.getRootPane().setOpaque(false);
		SwingUtils3.createDialogBackPanel(this,  owner.getContentPane());
	}
	
	public static void main(String[] args) {
		/*
		HostClassroomDialog dialog = new HostClassroomDialog();
		dialog.setVisible(true);
		dialog.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		
		Map<Integer, String> list = new HashMap<Integer, String>();
		list.put(5, "FIVE");
		list.put(20, "TWENTY");
		list.put(0, "ZERO");
		
		dialog.setClassrooms(list);
		*/
	}
	
	private void createGUI() {
		
		//setSize(300, 300);
		
		// List
		JPanel centerPanel = new JPanel();
		listModel = new DefaultListModel<String>();
		list = new JList<String>(listModel);
		
		listModel.addElement("Loading...");
		
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setEnabled(false);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				okayButton.setEnabled(list.getSelectedIndex() != -1);
			}
		});
		centerPanel.add(list);
		add(centerPanel, BorderLayout.CENTER);
		
		// Buttons
		JPanel buttonPanel = new JPanel();
		
		okayButton = new JButton("Create");
		okayButton.setEnabled(false);
		okayButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectedClassroomID = classroomIDs[list.getSelectedIndex()];
				setVisible(false);
				dispose();
			}
		});
		
		cancelButton = new JButton("Cancel");
		
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		
		buttonPanel.add(okayButton);
		buttonPanel.add(cancelButton);
		
		add(buttonPanel, BorderLayout.SOUTH);
		//Setting Background and foreground and text;
		AppearanceSettings.setBackground(Constants.maroon, list, okayButton, cancelButton);
		AppearanceSettings.setBackground(Constants.maroon, centerPanel,buttonPanel);
		AppearanceSettings.setForeground(Constants.gold, list, okayButton,cancelButton);
		AppearanceSettings.setForeground(Constants.gold, centerPanel,buttonPanel);
		AppearanceSettings.setFont(Constants.standardFont, list, okayButton, cancelButton);
			
		
	}
	
	public void setClassrooms(Map<Integer, String> list) {
		
		this.list.setEnabled(true);
		
		listModel.clear();
		classroomIDs = new int[list.size()];
		
		int i=0;
		for (int ID : list.keySet()) {
			classroomIDs[i++] = ID;
			listModel.addElement(list.get(ID));
		}
	}
	
	private int selectedClassroomID = -1;
	public int getSelectedClassroomID() {
		return selectedClassroomID;
	}
}