package client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import communication.LoginRequest;
import communication.LoginResponse;
import communication.Update;
import listeners.TextFocusListener;
import util.AppearanceSettings;

public class LoginGUI extends JFrame {

	private static final long serialVersionUID = -4491869616102286067L;
	private JTextField username, password;
	private JButton loginButton,registerButton,guestButton;
	private JPanel welcomePanel, textPanel, buttonPanel;
	public LoginGUI(){
		super("Login");
		initializeVariables();
		createGUI();
		addActionListeners();
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setVisible(true);
		
		this.setEnabled(false);
		
		boolean goodAddress = false;
		while (!goodAddress) {
			String address = (String)JOptionPane.showInputDialog(
	                this,
	                "Server Address:",
	                "Connect to Server",
	                JOptionPane.PLAIN_MESSAGE,
	                null,
	                null,
	                "localhost");
			
			goodAddress = UserClassroomCommunicator.setServerAddress(address);
		}
		
		this.setEnabled(true);
	}
	public void initializeVariables() {
		username = new JTextField();
		username.addFocusListener(new TextFocusListener("Username", username));
		password = new JTextField();
		password.addFocusListener(new TextFocusListener("Password", password));
		loginButton = new JButton("Login");
		registerButton = new JButton("Register");
		guestButton = new JButton("Continue as guest");
		//AppearanceSettings.setForeground(Color.BLACK, loginButton, registerButton, guestButton);
		//AppearanceSettings.setBackground(Color.LIGHT_GRAY, loginButton, registerButton, guestButton);
		
		//loginButton.setContentAreaFilled(false);
        //loginButton.setOpaque(true);
        
		
		welcomePanel = new JPanel();
		textPanel = new JPanel();
		buttonPanel = new JPanel(new GridLayout(1,3));
	}
	public void createGUI(){
		setLocation(600, 80);
		setSize(500,250);
		setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		JLabel login = new JLabel("Welcome to Codoceo!");
		JLabel userpass = new JLabel("Enter a username and password");
		welcomePanel.add(login);
		
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.add(Box.createVerticalStrut(getHeight()/30));
		textPanel.add(userpass);
		textPanel.add(username);
		textPanel.add(Box.createVerticalStrut(getHeight()/30));
		textPanel.add(password);
		textPanel.add(Box.createVerticalStrut(getHeight()/30));
		buttonPanel.add(loginButton);
		buttonPanel.add(registerButton);
		buttonPanel.add(guestButton);
		//Setting Background and foreground and text;
		
		AppearanceSettings.setForeground(Constants.maroon, loginButton, registerButton, guestButton);
		AppearanceSettings.setBackground(Color.LIGHT_GRAY, loginButton, registerButton, guestButton);
		AppearanceSettings.setBackground(Constants.maroon, login, userpass, username, password);
		AppearanceSettings.setBackground(Constants.maroon, welcomePanel, textPanel,buttonPanel);
		AppearanceSettings.setForeground(Constants.gold, login,userpass,username,password);
		AppearanceSettings.setFont(Constants.standardFont, login,userpass,username,password,loginButton,registerButton,guestButton);
		login.setFont(Constants.fontLarge);
		AppearanceSettings.setTextAlignment(login,userpass);
		
		add(welcomePanel);
		add(textPanel);
		add(buttonPanel);
		
	}
	
	public void addActionListeners(){
		
		class LoginResponseListener extends UserClassroomCommunicator.UpdateListener {
			public void updateRecieved(Update update) {
				if (update instanceof LoginResponse) {
					LoginResponse response = (LoginResponse) update;
					
					if (response.success) {
						System.out.println("Successful login/register!");
						
						dispose();
						HomeScreenGUI homeScreen = new HomeScreenGUI(response.user);
					}
					else
					{
						System.out.println("Failed to login/register.");
						// TODO: Failure message
						setEnabled(true);
					}
					
					remove = true;
				}
			}		
		}
		
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) { // request login 
				
				LoginRequest request = new LoginRequest(username.getText(), password.getText(), false);
				
				UserClassroomCommunicator.getInstance().addUpdateListener(new LoginResponseListener());
				UserClassroomCommunicator.getInstance().sendUpdate(request);
				
				setEnabled(false);
			}
		});
		registerButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // request create user
				LoginRequest request = new LoginRequest(username.getText(), password.getText(), true);
				UserClassroomCommunicator.getInstance().addUpdateListener(new LoginResponseListener());
				UserClassroomCommunicator.getInstance().sendUpdate(request);
				
				setEnabled(false);
			}
		});
		guestButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { // request authenticate guest
				
				LoginRequest request = new LoginRequest();
				
				UserClassroomCommunicator.getInstance().addUpdateListener(new LoginResponseListener());
				UserClassroomCommunicator.getInstance().sendUpdate(request);
			}
		});
	}
	
	public void setEnabled(boolean enabled) {
		username.setEnabled(enabled);
		password.setEnabled(enabled);
		
		loginButton.setEnabled(enabled);
		registerButton.setEnabled(enabled);
		guestButton.setEnabled(enabled);
	}
	
	public static void main(String[] args) {
				
		LoginGUI gui = new LoginGUI();
	}
	
}
