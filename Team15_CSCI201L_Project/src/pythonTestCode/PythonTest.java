package pythonTestCode;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class PythonTest{
	static ScriptEngine pyInt;
	
	static ScriptEngineManager factory = new ScriptEngineManager();
	static ScriptEngine jsInt;
	static int mode = 0;
	
	static class TestCase {
		public String call;
		public String expectedResult;
		
		public TestCase(String c, String r) {
			call = c;
			expectedResult = r;
		}
		
	}
	public static TestCase[] tests = new TestCase[]{
			new TestCase("sum(2, 2)", "4"),
			new TestCase("sum(0, 0)", "0"),
			new TestCase("sum(-1, 23)", "22")
	};
	
	public static void main(String[] args) {
		jsInt = factory.getEngineByName("JavaScript");
		pyInt = factory.getEngineByName("Python");
		JFrame frame = new JFrame("Python Test");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JComboBox<String> cb = new JComboBox<String>();
		cb.addItem("Python");
		cb.addItem("JavaScript");
		JTextArea area = new JTextArea();
		JPanel southPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridLayout(1, 2));
		southPanel.setLayout(new GridLayout(2, 1));
		JButton clear = new JButton("Clear");
		JButton compile = new JButton("Run");
		JTextField field = new JTextField();
		frame.setSize(300, 400);
		frame.setVisible(true);
		compile.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {					 
				if(mode==0){
					try {    
						pyInt.eval("from cStringIO import StringIO");
						pyInt.eval("import sys");
						pyInt.eval("sys.stdout = mystdout = StringIO()");
						pyInt.eval(area.getText());
						pyInt.eval("consoleOutput = mystdout.getvalue()");
						String output = pyInt.get("consoleOutput").toString();
						field.setText(output);
					} catch (ScriptException e1) {
						System.out.println("ScriptException: "+e1.getMessage());
					}
				}
				else{
					try {
						jsInt.eval("var s;");
						String input = area.getText().replace("print", "s = ");
						jsInt.eval(input);
						String s = jsInt.get("s").toString();
						field.setText(s);
					} catch (ScriptException e1) {
						System.out.println("ScriptException: "+e1.getMessage());
					}
				}
				
			}
		});
		cb.addItemListener(new ItemListener(){
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
		clear.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				field.setText("");
				area.setText("");
			}
		});
		buttonPanel.add(compile);
		buttonPanel.add(clear);
		southPanel.add(field);
		southPanel.add(buttonPanel);
		frame.add(southPanel, BorderLayout.SOUTH);
		frame.add(cb, BorderLayout.NORTH);
		frame.add(area);
	}

}
