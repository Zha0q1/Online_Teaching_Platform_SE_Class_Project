package client;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EtchedBorder;

import shared.Chatbox;
import shared.ChatboxUpdate;
import shared.Classroom;
import shared.User;
import util.AppearanceSettings;

public class ChatboxPanel extends ClassroomFeaturePanel<Chatbox> {

	private static final long serialVersionUID = 4510576683938306987L;
	private JTabbedPane chatTabs;
	private JTextField messageField;
	private JCheckBox sendMessageBox;
	private User recipient;
	JTextArea messageArea;
	JComboBox<String> cb;
	String msg = null;
	DefaultComboBoxModel<String> cbm;
	Vector<String> nameVect;
	//
	// Member Variables:
	//
	
	@Override
	public boolean isTab() { return false; }
	
	//
	// Methods:
	//
	
	public ChatboxPanel(Chatbox chatbox, Classroom classroom, User user) {
		
		super(chatbox, classroom, user);
		createGUI();
		recipient = null;
	}
	
	public void createGUI() {
		
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		if(amInstructor()){
			System.out.println(classroom==null?"null":"not null");
			System.out.println(classroom.getStudents()==null?"null":"not null");
			System.out.println(classroom.getStudents().size());
			nameVect = new Vector<String>();
			cbm = new DefaultComboBoxModel<String>(nameVect);
			cb = new JComboBox<String>(cbm);
			add(cb, BorderLayout.NORTH);
			cbm.addElement("Group");
			for(User student : classroom.getStudents()){
				cbm.addElement(student.getDisplayName());
			}
			cb.addItemListener(new ItemListener(){
				public void itemStateChanged(ItemEvent event) {
					if (event.getStateChange() == ItemEvent.SELECTED) {
				          String username = (String)event.getItem();
				          if(username=="Group"){
				        	  recipient = null;
				          }
				          else{
				        	  recipient = classroom.getUserByUsername(username);
				          }
				       }
				}
			});
		}
		
		JPanel southPanel = new JPanel();
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		add(southPanel, BorderLayout.SOUTH);
		
		chatTabs = new JTabbedPane();
		JPanel groupTab = new JPanel();
		messageArea = new JTextArea(15, 30);
		messageArea.setEditable(false);
		groupTab.add(messageArea);
		if (!amInstructor()){
			sendMessageBox = new JCheckBox("Send to instructor");
			southPanel.add(sendMessageBox);
			AppearanceSettings.setBackground(Constants.maroon, southPanel);
			sendMessageBox.addItemListener(new ItemListener() {
			      public void itemStateChanged(ItemEvent e) {
			        if(sendMessageBox.isSelected())
			        	recipient = classroom.getInstructor();
			        else
			        	recipient = null;
			      }
			    });
			groupTab.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new BevelBorder(BevelBorder.RAISED)));
			add(groupTab, BorderLayout.CENTER);
		}
		else{
			groupTab.setBorder(new CompoundBorder(new BevelBorder(BevelBorder.LOWERED), new BevelBorder(BevelBorder.RAISED)));
			add(groupTab, BorderLayout.CENTER);
		}
		
		messageField = new JTextField("Enter a message");
		if(user.isGuest()){
			messageField.setText("Guests can not use chatbox");
			messageField.setEditable(false);
		}
		messageField.addMouseListener(new MouseListener(){
			public void mouseClicked(MouseEvent arg0) {
				if(messageField.getText().equals("Enter a message"))
					messageField.setText("");
			}
			public void mouseEntered(MouseEvent arg0) {}
			public void mouseExited(MouseEvent arg0) {}
			public void mousePressed(MouseEvent arg0) {}
			public void mouseReleased(MouseEvent arg0) {}
		});
		messageField.addKeyListener(new KeyListener(){
			public void keyPressed(KeyEvent ke) {
				if(ke.getKeyCode()==KeyEvent.VK_ENTER){
					if(user.isGuest())
						return;
					messageArea.requestFocusInWindow();
					msg = messageField.getText();
					flushMessage();
				}
			}
			public void keyReleased(KeyEvent arg0) {}
			public void keyTyped(KeyEvent arg0) {}
		});
		southPanel.add(messageField);
		AppearanceSettings.setBackground(Constants.maroon, southPanel,groupTab, messageField);
		AppearanceSettings.setForeground(Constants.gold, messageField);

	
	}
	
	public void flushMessage(){
		feature.sendUpdate(new ChatboxUpdate(msg, user, recipient));
		messageField.setText("Enter a message");
	}
	
	public void addMessage(int to, int from, String message){
		System.out.println(to);
		System.out.println(from);
		if(to==-1)
			messageArea.append(classroom.getUserByID(from).getDisplayName()+ ": " + message +"\n");
		else
			messageArea.append(classroom.getUserByID(from).getDisplayName()+ "(Direct): " + message +"\n");
	}
	
	public void addUserToInstructorCB(ArrayList<User> u){
		if(!amInstructor())
			return;
		cbm.removeAllElements();
		System.out.println("Combobox model size: "+cbm.getSize());
		cbm.addElement("Group");
		for(User student : u){
			if(student.getDisplayName().contains("Guest"))
				continue;
			cbm.addElement(student.getDisplayName());
		}
		System.out.println("Combobox model size: "+cbm.getSize());
	}
	
}
