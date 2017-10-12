package client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import listeners.TextFocusListener;
import shared.User;
import util.AppearanceSettings;
import util.SwingUtils2;

public class CreateActivityDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JTextArea skeletonField;
	private JTextField nameField, inputField,outputField;
	private JButton createActivityButton, cancelButton;
	private JPanel centerPanel, buttonPanel;
	private Boolean activityChosen = false;
	private User userinfo;
	private JComboBox<String> languages;
	private int mode;
	
	public boolean success = false;
	
	public CreateActivityDialog(JFrame frame){
		super(frame, "Create Activity", true);
		initializeVariables();
		createGUI();
		setUndecorated(true);
		this.getRootPane().setOpaque(false);
		SwingUtils2.createDialogBackPanel(this,  frame.getContentPane());
		addEvents();
	}
	public void initializeVariables(){
		mode = 0;
		nameField = new JTextField(15);
		skeletonField = new JTextArea(20, 38);
		inputField = new JTextField(15);
		outputField = new JTextField(15);
		createActivityButton = new JButton("Create Activity");
		cancelButton = new JButton("Cancel");
		languages = new JComboBox<String>();
		languages.addItem("Python");
		languages.addItem("JavaScript");
		createActivityButton.setEnabled(false);
	}
	public void createGUI(){
		//setSize(300, 300);
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(3,1));
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(2,1));
		JLabel nameLabel = new JLabel("Name this activity");
		JLabel skeletonLabel = new JLabel("Provide skeleton code");
		JLabel inputLabel = new JLabel("Provide the test case with an input and output");
		JPanel namePanel = new JPanel();
		JPanel skeletonPanel = new JPanel();
		skeletonPanel.setLayout(new GridLayout(2,1));
		JPanel testCasePanel = new JPanel();
		testCasePanel.setLayout(new BoxLayout(testCasePanel, BoxLayout.Y_AXIS));
		namePanel.add(nameLabel);
		namePanel.add(nameField);
		skeletonPanel.add(skeletonLabel);
		JScrollPane scroll = new JScrollPane(skeletonField);
	    scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		skeletonPanel.add(scroll);
		testCasePanel.add(inputLabel);
		JPanel fields = new JPanel(new GridLayout(1,2));
		testCasePanel.add(fields);
		fields.add(inputField);
		fields.add(outputField);
		centerPanel.add(namePanel);
		centerPanel.add(skeletonPanel);
		centerPanel.add(testCasePanel);
		buttonPanel.add(cancelButton);
		buttonPanel.add(createActivityButton);
		JPanel northPanel = new JPanel();
		northPanel.add(languages);
		add(northPanel, BorderLayout.NORTH);
		add(centerPanel, BorderLayout.CENTER);
		add(buttonPanel,BorderLayout.SOUTH);
		//Setting Background and foreground and text;
		AppearanceSettings.setBackground(Constants.maroon, languages ,nameLabel, skeletonLabel,inputLabel,nameField, inputField,outputField);
		AppearanceSettings.setBackground(Constants.maroon, northPanel, namePanel, skeletonPanel,testCasePanel,fields, centerPanel,buttonPanel);
		AppearanceSettings.setForeground(Constants.gold, nameLabel, skeletonLabel, inputLabel, skeletonLabel, skeletonField,inputField,outputField);
		AppearanceSettings.setForeground(Constants.gold, languages,cancelButton,createActivityButton);
		AppearanceSettings.setFont(Constants.standardFont, nameLabel,skeletonLabel,inputLabel,skeletonLabel, cancelButton, createActivityButton);
		AppearanceSettings.setTextComponents(skeletonField);
		AppearanceSettings.setTextAlignment(nameLabel,inputLabel, skeletonLabel);
	}
	public void addEvents(){
		//setSize(400,300);
		this.setDefaultCloseOperation(this.DISPOSE_ON_CLOSE);
		skeletonField.getDocument().addDocumentListener(new MyDocumentListener());
		nameField.addFocusListener(new TextFocusListener("Activity name", nameField));
		nameField.getDocument().addDocumentListener(new MyDocumentListener());
		inputField.addFocusListener(new TextFocusListener("Input", inputField));
		inputField.getDocument().addDocumentListener(new MyDocumentListener());
		outputField.addFocusListener(new TextFocusListener("Output", outputField));
		outputField.getDocument().addDocumentListener(new MyDocumentListener());
		createActivityButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				//null should be changed to the activity chosen from the combobox
				success = true;
//				setVisible(false);
//				dispose();
				SwingUtils2.fadeOut(CreateActivityDialog.this);
			}
		});
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
//				setVisible(false);
//				dispose();
				SwingUtils2.fadeOut(CreateActivityDialog.this);
			}
		});
		
		languages.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
			          String type = (String)event.getItem();
			          if(type=="Python"){
			        	  mode = 0;
			          }
			          else{
			        	  mode = 1;
			          }
			       }
			}
			
		});

	}
	public String getActivityName(){
		return nameField.getText();
	}
	public String getSkeleton(){
		return skeletonField.getText();
	}
	public String getInput(){
		return inputField.getText();
	}
	public String getOutput(){
		return outputField.getText();
	}
	public Boolean canPressButton(){
		return (!nameField.getText().isEmpty() && !nameField.getText().equals("Activity Name") &&
				!skeletonField.getText().isEmpty() && !skeletonField.getText().equals("Add skeleton code") &&
				!inputField.getText().isEmpty() && !inputField.getText().equals("Input") &&
				!outputField.getText().isEmpty() && !outputField.getText().equals("Output"));
	}
	public int getMode(){
		return mode;
	}
	//sets the buttons enabled or disabled
		private class MyDocumentListener implements DocumentListener{
			
			@Override
			public void insertUpdate(DocumentEvent e) {
				createActivityButton.setEnabled(canPressButton());
			}
			
			@Override
			public void removeUpdate(DocumentEvent e) {
				createActivityButton.setEnabled(canPressButton());
			}
			
			@Override
			public void changedUpdate(DocumentEvent e) {
				createActivityButton.setEnabled(canPressButton());
			}
		}
}

