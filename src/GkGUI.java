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


import taskpanel.TaskPanel;

import goalkeeper.calendar.GkCalendarPanel;
import goalpanel.GoalPanel;


public class GkGUI extends JFrame{
	private TaskPanel taskPanel;
	private JLabel[]taskLabels;
	private Planner planner;
	private GkCalendarPanel calendarPanel;
	private GoalPanel goalPanel;
	private int guiDate;//yyyymmdd of currently displayed planner date
	
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
		CalendarListener calendarListener = new CalendarListener();
		calendarPanel.addActionListener(calendarListener);
		
		//configure taskPanel (daily to-do list display area)
		taskPanel = new TaskPanel();
		taskPanel.setBounds(5, 100, 300, 455);
		add(taskPanel);
		TaskListener taskListener = new TaskListener();
		taskPanel.addActionListener(taskListener);
		
		//configure goalPanel (currently active goals display area)
		goalPanel = new GoalPanel();
		goalPanel.setBounds(525, 5, 255, 550);
		goalPanel.setVisible(true);
		add(goalPanel);
		
		calendarPanel.ping();//get initially selected date to display (from calendarPanel)
	}
	
	public void execute(){
		setVisible(true);
		for(Component c : getComponents())
			c.setVisible(true);
	}
	//add a goal to the current planner
	public void addNewGoal(String n, Color c){
		planner.addNewGoal(n, c);
		goalPanel.addItem(n, c);
	}
	//actionlistener to respond to date selection event from calendarpanel
	public class CalendarListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			guiDate = Integer.valueOf(e.getActionCommand());
			System.out.println("The date is: " + guiDate);
			for(int x = 0;x < taskPanel.getListSize();x++){
				try{//if task exists for item slot in taskPanel, update item label
					taskPanel.setText(x, planner.getTask(guiDate,x).getName());
					taskPanel.setColor(x, planner.getTask(guiDate,x).getGoal().getColor());
					if(planner.getTask(guiDate,x).isCompleted())
						taskPanel.setChecked(x, true);
					else
						taskPanel.setChecked(x, false);
					taskPanel.setItemVisibility(x, true);
				}catch(IndexOutOfBoundsException e1){//if task DNE, hide item & its checkbox
					taskPanel.setItemVisibility(x, false);
				}catch(NullPointerException e2){}//if try block refers to attributes of nonexistant tasks, do nothing
			}
		}
	}
	//actionlistener to respond to checkbox selection event from TaskPanel
	public class TaskListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			String command = e.getActionCommand();
			//extract <index> and <checked> values from actionevent
			int taskIndex = Integer.valueOf(command.substring(0, command.indexOf(" ")));
			boolean checked = Boolean.valueOf(command.substring(command.indexOf(" ") + 1));
			//update task in planner: checked = completed, unchecked = not completed
			planner.getTasks(guiDate).get(taskIndex).setCompleted(checked);
		}
	}

		
}
