package goalkeeper.calendar;

import javax.swing.*;

public class GkCalendarPanelTest {

	public static void main(String[]args){
		JFrame testFrame = new JFrame();
		testFrame.setLayout(null);
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setSize(800,600);
		
		GkCalendarPanel cal = new GkCalendarPanel();
		cal.setLocation(200,200);
		testFrame.add(cal);
		
		testFrame.setVisible(true);
	
	}
}
