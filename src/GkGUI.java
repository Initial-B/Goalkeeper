import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.ArrayList;

import javax.swing.*;

import goalkeeper.calendar.GkCalendarPanel;


public class GkGUI extends JFrame{
	private TaskPanel taskPanel;
	private JLabel[]taskLabels;
	private Planner planner;
	private GkCalendarPanel calendarPanel;
	
	public void setPlanner(Planner p){planner = p;}
	
	public GkGUI(){
		//configure main GUI window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(null);// enable explicit positioning of GUI components
        contentPane.setBackground( Color.lightGray);
        setTitle("Goalkeeper");
        setSize(new Dimension(800,600));
        //super.setLocation(200,160);
        
        //configure calendarPanel
        calendarPanel = new GkCalendarPanel();
        calendarPanel.setLocation(310,5);
		add(calendarPanel);
		DateListener listener = new DateListener();
		calendarPanel.addActionListener(listener);
		
		//configure taskPanel (daily to-do list display area)
		taskPanel = new TaskPanel();
		taskPanel.setBounds(5, 100, 300, 456);
		add(taskPanel);
		
		
	}
	
	public void execute(){
		setVisible(true);
		for(Component c : getComponents())
			c.setVisible(true);
	}

	//actionlistener to read date from calendarpanel
	public class DateListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			System.out.println("The date is: " + e.getActionCommand());
		}
	}
	//(component) JPanel with white bg, regular JLabels separated by dotted lines,
	//				and a left side column for checkboxes
	private class TaskPanel extends JPanel{
		final static private int itemSpacing = 38; //default pixel spacing for items 
		private ArrayList<JLabel> itemList;
		
		public TaskPanel(){
			setBackground(Color.white);
			setLayout(null);
		
		}
		//(override) paint inner area of numPanel based on value of isSelected
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
			Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{5}, 0);
			Graphics2D g2d = (Graphics2D)g;
			g2d.setStroke(dashedStroke);
			for(double y = itemSpacing; y < getHeight();y+=itemSpacing){
				Line2D line = new Line2D.Double(0, y, getWidth(), y);
				g2d.draw(line);
			}
		
		}
		
	}
	
}
