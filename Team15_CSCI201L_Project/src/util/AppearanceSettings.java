package util;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

public class AppearanceSettings {
	
	//sets background color of JComponents passed in
	@SafeVarargs
	public static <T extends JComponent>void setBackground(Color backGround, T ... components ){
		
		for (T component : components) component.setBackground(backGround);
	}
	
	//sets foreground of supplied JComponents
	@SafeVarargs
	public static <T extends JComponent>void setForeground(Color foreGround, T ... components ){
		
		for (T component : components) component.setForeground(foreGround);
	}
	
	//sets font of supplied JComponents
	@SafeVarargs
	public static <T extends JComponent>void setFont(Font font, T ... components ){
		
		for (T component : components) component.setFont(font);
	}
	
	//centers the text for the passed in labels
	@SafeVarargs
	public static void setTextAlignment(JLabel ... labels ){

		for (JLabel label : labels) label.setHorizontalAlignment(SwingConstants.CENTER);
	}

	//JTextArea settings
	public static void setTextComponents(JTextArea... components){

		for (JTextArea component : components){
			component.setLineWrap(true);
			component.setWrapStyleWord(true);
			component.setOpaque(true);
		}
	}

	//sets components opaque
	@SafeVarargs
	public static <T extends JComponent>void setOpaque(T ... components ){

		for (T component : components) component.setOpaque(true);
	}

	
	//sets size of components
	@SafeVarargs
	public static <T extends JComponent> void setSize(int x, int y, T ...components){
		
		Dimension dim = new Dimension(x, y);
		
		for (T component: components){
			component.setPreferredSize(dim);
		}
	}
	//method to remove the border on buttons
	public static void unSetBorderOnButtons(JButton ... buttons){

		for (JButton button: buttons) button.setBorderPainted(false);
	}
}
