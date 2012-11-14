package goalkeeper.calendar;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class GkCalendarPanelTest {
	GkCalendarPanel cal;
	JLabel dateLabel;
	JFrame testFrame;
	int eventCount;
	
	public GkCalendarPanelTest(){
		testFrame = new JFrame();
		testFrame.setLayout(null);
		testFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testFrame.setSize(800,600);
		
		cal = new GkCalendarPanel();
		cal.setLocation(200,200);
		testFrame.add(cal);
		
		
		dateLabel = new JLabel("hello");
		dateLabel.setBounds(1,1,200,25);
		dateLabel.setVisible(true);
		testFrame.add(dateLabel);
		
		DateListener listener = new DateListener();
		cal.addActionListener(listener);

		testFrame.setVisible(true);
		
		eventCount = 0;
	}
	public class DateListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			dateLabel.setText("The date is: " + e.getActionCommand());
			eventCount++;
			System.out.println(eventCount);
		}
	}

	public static void main(String[]args){
		new GkCalendarPanelTest();
	}
}
