package listeners;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;

import client.Constants;
public class TextFocusListener implements FocusListener{

	private String defaultText;
	private JTextField thisTextField;
	
	public TextFocusListener(String defaultText, JTextField thisTextField) {
		this.defaultText = defaultText;
		this.thisTextField = thisTextField;
		thisTextField.setText(defaultText);
		thisTextField.setForeground(Constants.gold);
	}

	@Override
    public void focusGained(FocusEvent fe)
    {
		Font font = new Font("SansSerif", Font.BOLD,16);
        
        //set font for JTextField
        thisTextField.setFont(font);
		thisTextField.setForeground(Constants.gold);
        thisTextField.setText("");
	    
    }

    @Override
    public void focusLost(FocusEvent fe)
    {
    	if (thisTextField.getText().equals("")){
    		thisTextField.setForeground(Constants.gold);
	    	thisTextField.setText(defaultText);
    	}
    
    }
}
