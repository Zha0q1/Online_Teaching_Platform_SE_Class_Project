package client;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import shared.Classroom;
import shared.Classroom.ClassroomListener;
import util.AppearanceSettings;
import shared.User;

public class ClassroomInfoPanel extends JPanel implements ClassroomListener {
	
	private static final long serialVersionUID = -1828123432694632696L;
	
	//
	// Member Variables:
	//
	
	private JLabel nameLabel;
	private JLabel instructorLabel;
	private JLabel studentsLabel;
	
	private Classroom classroom;
	private User user;
	
	//
	// Methods:
	//
	
	public ClassroomInfoPanel(Classroom classroom, User user) {
		
		this.classroom = classroom;
		this.user = user;
		
		createGUI();		
	}
	
	private void createGUI() {
		
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		setLayout(new GridLayout(3, 1));
		
		nameLabel = new JLabel("Name");
		nameLabel.setFont(Constants.standardFont.deriveFont(Font.BOLD));
		nameLabel.setHorizontalAlignment(JLabel.CENTER);
		
		instructorLabel = new JLabel("Instructor");
		instructorLabel.setFont(Constants.standardFont);
		instructorLabel.setHorizontalAlignment(JLabel.CENTER);
		
		studentsLabel = new JLabel("Students");
		studentsLabel.setFont(Constants.standardFont);
		studentsLabel.setHorizontalAlignment(JLabel.CENTER);
		
		classroomInfoUpdated();
		AppearanceSettings.setForeground(Constants.gold, nameLabel, instructorLabel, studentsLabel);
		add(nameLabel);
		add(instructorLabel);
		add(studentsLabel);
	}

	@Override
	public void classroomInfoUpdated() {
		nameLabel.setText(classroom.getName());
		
		instructorLabel.setText("Instructor: " + classroom.getInstructor().getUsername());
		
		studentsLabel.setText(classroom.getStudents().size() + " student(s) in classroom.");
	}
}
