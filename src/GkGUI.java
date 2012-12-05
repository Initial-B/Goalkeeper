import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;


import taskpanel.*;

import goalkeeper.calendar.GkCalendarPanel;
import goalpanel.GoalPanel;


public class GkGUI extends JFrame{
	final static String[] months = {"January","February","March","April","May","June",
		"July","August","September","October","November","December"};
	final static String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	final static Color[] goalColors = {//suppported goal colors (values from HTML + X11 standard colors)
		/*red            */new Color(255,  0,  0),	/*maroon         */new Color(128,  0,  0),
		/*orange         */new Color(255,165,  0),	/*magenta        */new Color(255,  0,255),
		/*yellow         */new Color(255,255,  0),	/*cyan           */new Color(  0,255,255),
		/*green          */new Color(  0,128,  0),	/*lime green     */new Color(  0,255,  0),
		/*blue           */new Color(  0,  0,255),	/*navy blue      */new Color(  0,  0,128),
		/*purple         */new Color(128,  0,128),	/*turquoise      */new Color( 64,224,208),
		/*light gray     */new Color(192,192,192),	/*plum           */new Color(221,160,221),
		/*dark gray      */new Color(128,128,128),	/*coral          */new Color(255,127, 80),
		/*saddle brown   */new Color(139, 69, 19),	/*dark slate gray*/new Color( 47, 79, 79),
		/*goldenrod      */new Color(218,165, 32),	/*dark khaki     */new Color(189,183,107),
	};

	private ArrayList<ImageIcon> goalIcons;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	
	private TaskPanel taskPanel;
	private JLabel nameLabel;//displays planner name
	private JLabel dateLabel;//displays currently selected date
	private GkCalendarPanel calendarPanel;
	private GoalPanel goalPanel;
	
	private JButton newTaskButton, newGoalButton, editTasksButton, editGoalsButton,
		saveChangesButton, goalTrackingButton;
	
	private Planner planner;
	private int guiDate;//yyyymmdd of currently displayed planner date
	private ArrayList<Color> availableColors;
	
	public GkGUI(){
		//configure main GUI window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container contentPane = getContentPane();
		contentPane.setLayout(null);// enable explicit positioning of GUI components
        contentPane.setBackground( Color.lightGray);
        setTitle("Goalkeeper");
        setSize(new Dimension(800,620));
        super.setLocation(350,100);
        
        //configure menuBar
        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        //configure fileMenu
        fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		menuBar.add(fileMenu);
        
        //configure nameLabel
        nameLabel = new JLabel("");
        nameLabel.setBounds(10,5,300,36);
        nameLabel.setFont(new Font("Dialog",0,28));
        nameLabel.setVisible(true);
        add(nameLabel);
        
        //configure dateLabel
        dateLabel = new JLabel("");
        dateLabel.setBounds(10,75,300,25);
        dateLabel.setFont(new Font("Dialog",0,18));
        dateLabel.setVisible(true);
        add(dateLabel);
        
        //configure calendarPanel
        calendarPanel = new GkCalendarPanel();
        calendarPanel.setLocation(310,5);//using default dimensions (210 x 160)
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
		
		goalIcons = new ArrayList<ImageIcon>();
		availableColors = new ArrayList<Color>();
		for(Color c : goalColors)//all colors initially available
			availableColors.add(c);
		
		calendarPanel.ping();//get initially selected date to display (from calendarPanel)
		
		//configure newTaskButton
		newTaskButton = new JButton("New Task");
		newTaskButton.setBounds(310, 170, 104, 50);
		newTaskButton.setVisible(true);
		newTaskButton.addActionListener(
			new ActionListener(){// anonymous inner class
				JTextField taskNameField = new JTextField();
				JLabel taskNameFieldLabel = new JLabel("Task Name");
				JLabel goalBoxLabel = new JLabel("Select Related Goal");
				public void actionPerformed(ActionEvent event) {
					JComponent[] inputs = new JComponent[]{
						taskNameFieldLabel, taskNameField,
						goalBoxLabel, createGoalComboBox()
					};
					JOptionPane.showMessageDialog(null, inputs, "Create new task", JOptionPane.PLAIN_MESSAGE);
				}
			} // end anonymous inner class
		); // end call to addActionListener
		add(newTaskButton);
		
		//configure newGoalButton
		newGoalButton = new JButton("New Goal");
		newGoalButton.setBounds(416, 170, 104, 50);
		newGoalButton.setVisible(true);
		newGoalButton.addActionListener(
				new ActionListener(){// anonymous inner class
					JTextField taskNameField = new JTextField();
					JLabel goalNameFieldLabel = new JLabel("Goal Name");
					JLabel colorBoxLabel = new JLabel("Select Color");
					public void actionPerformed(ActionEvent event) {
						JComponent[] inputs = new JComponent[]{
							goalNameFieldLabel, taskNameField,
							colorBoxLabel, createColorChoiceComboBox()
						};
						JOptionPane.showMessageDialog(null, inputs, "Create new goal", JOptionPane.PLAIN_MESSAGE);
					}
				} // end anonymous inner class
			); // end call to addActionListener
		add(newGoalButton);
		
		//configure editTasksButton
		editTasksButton = new JButton("Edit Tasks");
		editTasksButton.setBounds(310, 222, 210, 40);
		editTasksButton.setVisible(true);
		add(editTasksButton);
		
		//configure editGoalsButton
		editGoalsButton = new JButton("Edit Goals");
		editGoalsButton.setBounds(310, 264, 210, 40);
		editGoalsButton.setVisible(true);
		add(editGoalsButton);
		
		//configure saveChangesButton
		saveChangesButton = new JButton("Save Changes");
		saveChangesButton.setBounds(310, 306, 210, 40);
		saveChangesButton.setVisible(true);
		add(saveChangesButton);
		
		//configure goalTrackingButton
		goalTrackingButton = new JButton("Goal Tracking");
		goalTrackingButton.setBounds(310, 348, 210, 40);
		goalTrackingButton.setVisible(true);
		add(goalTrackingButton);

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
		availableColors.remove(c);//prevent future goals from using same color
		goalIcons.add(createColorIcon(20, 20, c));//
	}
	
	//set planner & update gui
	public void setPlanner(Planner p){
		planner = p;
		calendarPanel.ping();
		nameLabel.setText(planner.getName());
	}
	
	//update dateLabel
	private void updateDate(){dateLabel.setText(calendarPanel.getDateString());}
	

	//actionlistener to respond to date selection event from calendarpanel
	public class CalendarListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			updateDate();
			guiDate = Integer.valueOf(e.getActionCommand());
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
	
	//returns an ImageIcon of the specified width, height, and color
	private ImageIcon createColorIcon(int width, int height, Color color){
		JPanel colorSquare = new JPanel();
		colorSquare.setBounds(0, 0, width, height);
		colorSquare.setBackground(color);
		colorSquare.doLayout();
		colorSquare.validate();
		BufferedImage img = new BufferedImage(colorSquare.getWidth(), colorSquare.getHeight(), BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		colorSquare.paint(g);
		ImageIcon icon = new ImageIcon(img);
		return icon;
	}
	//returns a JComboBox featuring the available colors
	private JComboBox createColorChoiceComboBox(){
		ImageIcon[]availableColorIcons = new ImageIcon[availableColors.size()];
		for(int x = 0;x < availableColorIcons.length;x++)
			availableColorIcons[x] = createColorIcon(200, 20, availableColors.get(x));
		JComboBox colorBox = new JComboBox(availableColorIcons);
		return colorBox;
	}
	
	//returns a JComboBox featuring the current goals
	private JComboBox createGoalComboBox(){
		JComboBox goalBox = new JComboBox(planner.getGoalNames());
		goalBox.setRenderer(new GoalComboBoxRenderer());
		return goalBox;
	}
	//(inner class)custom comboBox cellRenderer for goal/color selection
	//	displays combo box items as JLabels formed from goal ImageIcons + goal names
	private class GoalComboBoxRenderer extends JLabel implements ListCellRenderer{	
		public GoalComboBoxRenderer(){setOpaque(true);}//JLabel must be opaque to use in comboBox
		//(override) implementation of ListCellRenderer
		public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus){
			//set cell background/foreground
            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            setForeground(isSelected ? list.getSelectionForeground() : list.getForeground());
            try{setIcon(goalIcons.get(index));}
				catch(ArrayIndexOutOfBoundsException e){//item displayed in combo box is index -1
					//retrieve correct index from planner using name of goal
					setIcon(goalIcons.get(Arrays.asList(planner.getGoalNames()).indexOf(value)));
				}
			setText((String)value);
			setFont(list.getFont());
			return this;
		}	
	}	
}
