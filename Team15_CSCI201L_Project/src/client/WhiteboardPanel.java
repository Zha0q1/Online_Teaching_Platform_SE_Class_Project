package client;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import shared.Classroom;
import shared.Pair;
import shared.User;
import shared.Whiteboard;
import shared.WhiteboardUpdate;

public class WhiteboardPanel extends ClassroomFeaturePanel<Whiteboard> implements MouseListener, MouseMotionListener{

	private static final long serialVersionUID = -2910705542392760939L;
	
	Vector<Pair<Color, Vector<Pair<Integer, Integer>>>> lines;
	Color currColor = Color.BLACK;
	JButton whiteButton;
	JButton blackButton;
	JButton redButton;
	
	//
	// Methods:
	//
	
	public WhiteboardPanel(Whiteboard whiteboard, Classroom classroom, User user) {
		
		super(whiteboard, classroom, user);
		this.lines = whiteboard.lines;
		setBackground(Color.WHITE);
		addMouseListener(this);
		addMouseMotionListener(this);
		createGUI();
		addActionListeners();
	}
	
	public void createGUI() {
		setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
		JPanel northPanel = new JPanel();
		northPanel.setLayout(new FlowLayout());
		northPanel.setBackground(Color.GRAY);
		whiteButton = new JButton();
		whiteButton.setBackground(Color.WHITE);
		blackButton = new JButton();
		blackButton.setBackground(Color.BLACK);
		redButton = new JButton();
		redButton.setBackground(Color.RED);
		northPanel.add(whiteButton);
		northPanel.add(blackButton);
		northPanel.add(redButton);
		add(northPanel, BorderLayout.NORTH);
	}
	
	public void addActionListeners(){
		whiteButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				currColor = Color.WHITE;
			}
		});
		blackButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				currColor = Color.BLACK;
			}
		});
		redButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				currColor = Color.RED;
			}
		});
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		
		lines.size();
		
		for(Pair<Color, Vector<Pair<Integer, Integer>>> line : lines){
			g2d.setColor(line.getKey());
			for(int i = 1; i < line.getValue().size(); i++){
				g2d.drawLine(line.getValue().elementAt(i-1).getKey(), line.getValue().elementAt(i-1).getValue(), line.getValue().elementAt(i).getKey(), line.getValue().elementAt(i).getValue());
			}
		}
	}
	
	public void addLineSegment(Pair<Color, Vector<Pair<Integer, Integer>>> line, int sentFromID){
		//if(sentFromID==user.getID())
		//	return;
		lines.add(line);
		repaint();
	}
	
	public void mouseClicked(MouseEvent ev) {}
	public void mouseEntered(MouseEvent arg0) {}
	public void mouseExited(MouseEvent arg0) {}
	public void mousePressed(MouseEvent arg0) {
		if(!amInstructor())
			return;
		lines.add(new Pair<Color, Vector<Pair<Integer, Integer>>>(currColor, new Vector<Pair<Integer, Integer>>()));
	}
	public void mouseReleased(MouseEvent arg0) {
		if(!amInstructor())
			return;
		feature.sendUpdate(new WhiteboardUpdate(lines.elementAt(lines.size()-1), user));
	}
	public void mouseDragged(MouseEvent ev) {
		if(!amInstructor())
			return;
		lines.elementAt(lines.size()-1).getValue().add(new Pair<Integer, Integer>(ev.getX(), ev.getY()));
		repaint();
	}
	public void mouseMoved(MouseEvent arg0) {}
}
