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
	
	public void setPlanner(Planner p){
		planner = p;
		calendarPanel.ping();
	}
	
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
			int ymdInt = Integer.valueOf(e.getActionCommand());
			System.out.println("The date is: " + ymdInt);
			for(int x = 0;x < taskPanel.itemList.size();x++){
				try{//if task exists for item slot in taskPanel, update item label
					taskPanel.itemList.get(x).setText(planner.getTasks(ymdInt).get(x).getName());
				}catch(IndexOutOfBoundsException exception){//if task DNE, set label's text blank
					taskPanel.itemList.get(x).setText("");
				}
			taskPanel.itemList.get(x).repaint();
			}
		}
	}
	//(component) JPanel with white bg, regular JLabels separated by dotted lines,
	//				and a left side column for checkboxes
	private class TaskPanel extends JPanel{
		final static private int itemSpacing = 38; //default pixel spacing for items 
		final static private int itemFontSize = 12;//default itemLabel font size
		private ArrayList<JLabel> itemList;
		
		public TaskPanel(){
			itemList = new ArrayList<JLabel>();
			setBackground(Color.white);
			setLayout(null);
		}
		
		//(override) paint inner area of numPanel based on value of isSelected
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			g.drawRect(0, 0, getWidth()-1, getHeight()-1);
			Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0);
			Graphics2D g2d = (Graphics2D)g;
			g2d.setStroke(dashedStroke);
			//place regular JLabels from itemList separated by horizontal lines
			for(double y = 0; y < getHeight();y+=itemSpacing){
				Line2D line = new Line2D.Double(0, y, getWidth(), y);
				g2d.draw(line);
			}
			dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
			g2d.setStroke(dashedStroke);
			Line2D line = new Line2D.Double(itemSpacing, 0, itemSpacing, getHeight());
			g2d.draw(line);
			
		}
		//instantiate JLabels in itemList based on component size
		public void fillItems(){
			itemList.clear();
			for(double y = 0; y < getHeight();y+=itemSpacing){
				JLabel itemLabel =  new JLabel("");
				itemLabel.setBounds((int)(1.2*itemSpacing), (int)(.2*itemSpacing + y),
						(int)(getWidth() - 1.3*itemSpacing), (int)(.75*itemSpacing));
				itemLabel.setFont(new Font("Dialog", Font.PLAIN, itemFontSize));
				itemLabel.setVisible(true);
				itemList.add(itemLabel);
				add(itemLabel);
			}
			repaint();
		}
		
		//(override) set bounds and fill itemLabels
		public void setBounds(int x, int y, int w, int h){
			super.setBounds(x,y,w,h);
			fillItems();
		}
		
		
		
	}
	
}
