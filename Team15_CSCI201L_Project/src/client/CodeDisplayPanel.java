package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import shared.Classroom;
import shared.CodeDisplay;
import shared.CodeDisplayUpdate;
import shared.User;

public class CodeDisplayPanel extends ClassroomFeaturePanel<CodeDisplay> {

	private static final long serialVersionUID = -8485017276609949139L;
	private JTextArea codeArea;
	private String text;
	//
	// Methods:
	//
	
	public CodeDisplayPanel(CodeDisplay codeDisplay, Classroom classroom, User user) {	
		
		super(codeDisplay, classroom, user);
		
		setName("Code Display");
		
		createGUI();
		addEvents();
		
		codeArea.setText(codeDisplay.getCode());
	}
	
	public void createGUI() {
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		
		setLayout(new BorderLayout());
		
		codeArea = new JTextArea();
		if (amInstructor()){
			codeArea.setEditable(true);
		}else{
			codeArea.setEditable(false);
		}
		
		this.setBackground(Constants.maroon);
		codeArea.setBackground(Color.GRAY.brighter());
		codeArea.setFont(codeArea.getFont().deriveFont(18f));
		
		add(codeArea, BorderLayout.CENTER);
	}
	public void addText(int receiver, int sender, String message){
		codeArea.setText(message);
		//if(user.getID()==receiver || user.getID()==-1){
//		if(message.equals("BACKSPACE"))
//			if (codeArea.getText().length()>=0) {
//				codeArea.setText(codeArea.getText().substring(0, codeArea.getText().length()-2));
//			}
//		else
//			codeArea.append(message);
		//}
	}
	public void addEvents(){
		codeArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void insertUpdate(DocumentEvent e) {
				feature.sendUpdate(new CodeDisplayUpdate(codeArea.getText(), user));
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				feature.sendUpdate(new CodeDisplayUpdate(codeArea.getText(), user));
			}
			@Override
			public void changedUpdate(DocumentEvent e) {
			}
		});
		
//		codeArea.addKeyListener(new KeyListener(){
//
//			@Override
//			public void keyTyped(KeyEvent e) {
//				// TODO Auto-generated method stub
//				
//			}
//
//			@Override
//			public void keyPressed(KeyEvent e) {
//				// TODO Auto-generated method stub
//				if(e.getKeyCode()==KeyEvent.VK_BACK_SPACE)
//					text = "BACKSPACE";
//				else
//					text = String.valueOf(e.getKeyChar());
//				System.out.println("code dispaly update");
//				feature.sendUpdate(new CodeDisplayUpdate(text, user));
//			}
//
//			@Override
//			public void keyReleased(KeyEvent e) {
//				// TODO Auto-generated method stub
//			}
//			
//		});
	}
}
