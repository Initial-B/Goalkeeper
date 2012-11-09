package goalkeeper.calendar;

import javax.swing.*;

public class GkCalendarPanelTest {

	public static void main(String[]args){
		JFrame testFrame = new JFrame();
		testFrame.setLayout(null);
		testFrame.setSize(800,600);
		
		GkCalendarPanel cal = new GkCalendarPanel();
		testFrame.add(cal);
		
		testFrame.setVisible(true);
	
	}
}
