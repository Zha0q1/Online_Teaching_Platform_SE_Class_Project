package client;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import communication.TabChangeUpdate;
import util.AppearanceSettings;

public class TabPanel extends JPanel {

	private static final long serialVersionUID = -3005499440416698562L;
	
	//
	// Member Variables:
	//
		
	private JTabbedPane tabbedPane;
	private JPanel contentPanel;
	private JButton lockButton;
	
	private boolean locked = true;
	private int suggestedTabIndex = 0;
	
	boolean instructor;
	
	//
	// Methods:
	//
	
	public TabPanel(boolean instructor) {
		
		this.instructor = instructor;
		
		// Layout GUI:
		
		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont(Constants.standardFont.deriveFont(Font.BOLD));
		//tabbedPane.setMaximumSize(new Dimension(Short.MAX_VALUE, 0));
		
		contentPanel = new JPanel(new CardLayout());
		contentPanel.setBackground(Color.YELLOW);
		
		lockButton = new JButton();
		
		layout.setHorizontalGroup(
			layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addGroup(
					layout.createSequentialGroup()
						.addComponent(tabbedPane)
						.addComponent(lockButton)
					)
				.addComponent(contentPanel)
		);
		
		int tabbedPaneHeight = (int) (Constants.standardFontSize * 1.6);
		
		layout.setVerticalGroup(
			layout.createSequentialGroup()
				.addGroup(
					layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(tabbedPane, tabbedPaneHeight, tabbedPaneHeight, tabbedPaneHeight)
						.addComponent(lockButton)
					)
				.addComponent(contentPanel)
		);
		
		add(tabbedPane);
		
		// Add Functionality:
		
		tabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {				
				changeTabIndex(tabbedPane.getSelectedIndex());
			}
		});
		
		lockButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!instructor) setLocked(!locked);
			}
		});
		setLocked(true);
		AppearanceSettings.setBackground(Constants.maroon, tabbedPane, contentPanel,lockButton);
		AppearanceSettings.setForeground(Constants.gold, tabbedPane, contentPanel, lockButton);
	}
	
	// Add a tab
	public void addTab(String name, JPanel component) {
		contentPanel.add(component, Integer.toString(tabbedPane.getTabCount()));
		
		tabbedPane.addTab(name, null);
	}
	
	// Call this method when the instructor changes tabs.
	public void suggestTabIndex(int index) {
		
		suggestedTabIndex = index;
		
		if (locked) changeTabIndex(index);	
	}
	
	// Actually change tabs
	private void changeTabIndex(int index) {
		
		if (index != suggestedTabIndex) setLocked(false);
		
		tabbedPane.setSelectedIndex(index);
		
		CardLayout cl = (CardLayout)contentPanel.getLayout();
		cl.show(contentPanel, Integer.toString(index));
		
		if (instructor) {
			UserClassroomCommunicator.getInstance().sendUpdate(new TabChangeUpdate(index));
		}
	}
	
	// Set whether or not the panel is in "locked" mode	
	public void setLocked(boolean locked) {
		
		
		if (instructor) {
			lockButton.setText("Instructor");
			this.locked = false;
		} else {
			lockButton.setText(locked ? "Unlock" : "Lock");
			this.locked = locked;
		}
		
		if (locked && suggestedTabIndex < tabbedPane.getTabCount()) changeTabIndex(suggestedTabIndex);
	}
}