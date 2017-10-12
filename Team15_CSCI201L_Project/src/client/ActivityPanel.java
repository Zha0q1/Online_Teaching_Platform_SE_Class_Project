package client;
 
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.python.util.PythonInterpreter;

//import pythonTestCode.PythonTest.TestCase;
import shared.Activity;
import shared.ActivityUpdate;
import shared.Classroom;
import shared.User;
import util.AppearanceSettings;

public class ActivityPanel extends ClassroomFeaturePanel<Activity> {

	private static final long serialVersionUID = -3860912476772806900L;
	private JButton clearButton, runButton, submitButton;
	private JTextArea codeArea;
	private JTextField output;
	private ScriptEngineManager factory = new ScriptEngineManager();
	private ScriptEngine jsInt;
	private PythonInterpreter pyInt;
	private Activity act;
	private JList<String> studentList;
	DefaultListModel<String> listModel;
	//
	// Methods:
	//
	
	public ActivityPanel(Activity activity, Classroom classroom, User user) {
		
		super(activity, classroom, user);
		act = activity;
		if(!amInstructor()){
			initializeVariables();
			createActivityPanel();
			addActionListeners();
		}
		else{
			createGUIInstructor();
		}
	}
	public void initializeVariables(){
		pyInt = new PythonInterpreter();
		jsInt = factory.getEngineByName("JavaScript");
		clearButton = new JButton("Clear");
		runButton = new JButton("Run");
		submitButton = new JButton("Submit");
		codeArea = new JTextArea();
		output = new JTextField();
		
	}
	public void createActivityPanel(){
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(codeArea);
		codeArea.setText(act.getSkeletonCode());
		codeArea.setPreferredSize(new Dimension(getWidth(), 270));
		JPanel southPanel = new JPanel();
		JPanel buttonPanel = new JPanel(new GridLayout(1,3));
		southPanel.setLayout(new BoxLayout(southPanel, BoxLayout.Y_AXIS));
		if(user.isGuest()){
			codeArea.setEditable(false);
			runButton.setEnabled(false);
			clearButton.setEnabled(false);
			submitButton.setEnabled(false);
		}
		buttonPanel.add(runButton);
		buttonPanel.add(clearButton);
		buttonPanel.add(submitButton);
		southPanel.add(output);
		southPanel.add(buttonPanel);
		add(southPanel, BorderLayout.SOUTH);
		AppearanceSettings.setBackground(Constants.maroon, buttonPanel, southPanel, codeArea, output);
		AppearanceSettings.setForeground(Constants.gold, clearButton, runButton, submitButton, output, codeArea);
		AppearanceSettings.setFont(Constants.standardFont, clearButton,runButton,submitButton,output,codeArea);
		codeArea.setFont(Constants.fontLargest);
	}
	public void addActionListeners(){
		clearButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				codeArea.setText(act.getSkeletonCode());
			}
		});
		runButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(act.getMode()==0){
				    
					pyInt.exec("from cStringIO import StringIO");
					pyInt.exec("import sys");
					pyInt.exec("sys.stdout = mystdout = StringIO()");
					pyInt.exec(codeArea.getText());
					pyInt.exec("consoleOutput = mystdout.getvalue()");
					String out = pyInt.get("consoleOutput").toString();
					output.setText(out);
				}
				else{
					try {
						jsInt.eval("var s;");
						String input = codeArea.getText().replace("print", "s = ");
						jsInt.eval(input);
						String s = jsInt.get("s").toString();
						output.setText(s);
					} catch (ScriptException e1) {
						output.setText("Error in script!");
					}
				}
			}
		});
		submitButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(act.getMode()==0){
					String s = pyInt.eval(act.getInput()).toString();
					if(act.getOutput().equals(s)){
						feature.sendUpdate(new ActivityUpdate(user));
						output.setText("Test cases passed!");
					}
					else
						output.setText("Test cases did not pass");
				}
				else{
					try {
						String s = (String)jsInt.eval(act.getInput());
						if(act.getOutput().equals(s)){
							feature.sendUpdate(new ActivityUpdate(user));
							output.setText("Test cases passed!");
						}
						else
							output.setText("Test cases did not pass");
					} catch (ScriptException e1) {
						output.setText("Error in script!");
					}
				}
				
			}
		});
	}
	
	public void createGUIInstructor(){
		setLayout(new GridLayout(1,1));
		listModel = new DefaultListModel<String>();
		studentList = new JList<String>(listModel);
		studentList.setVisible(true);
		
		studentList.setBackground(Color.GRAY.brighter());
		
		add(studentList);
		setBorder(BorderFactory.createTitledBorder("Completed Activity"));
	}
	
	public void addToCompletedStudents(int id){
		User u = classroom.getUserByID(id);
		System.out.println("User name: "+u.getDisplayName());
		listModel.addElement(u.getDisplayName());
		studentList.setVisible(false);
		studentList.setVisible(true);
	}
	
}
