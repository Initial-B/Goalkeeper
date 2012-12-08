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
	private final static String[] months = {"January","February","March","April","May","June",
		"July","August","September","October","November","December"};
	private final static String[] days = {"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
	private final static Color[] goalColors = {//suppported goal colors (names/values from HTML & X11 standard colors)
		/*red             */new Color(255,  0,  0),	
		/*coral           */new Color(255,127, 80),
		/*orange          */new Color(255,165,  0),	
		/*yellow          */new Color(255,255,  0),	
		/*lime green      */new Color(  0,255,  0),	
		/*olive drab      */new Color(107,142, 35),
		/*green           */new Color(  0,128,  0),	
		/*dark slate gray */new Color( 47, 79, 79),
		/*teal	          */new Color(  0,128,128),	
		/*cyan            */new Color(  0,255,255),
		/*blue            */new Color(  0,  0,255),	
		/*navy blue       */new Color(  0,  0,128),
		/*purple          */new Color(128,  0,128),	
		/*hot pink        */new Color(255,105,180),	
		/*magenta         */new Color(255,  0,255),	
		/*maroon          */new Color(128,  0,  0),
		/*saddle brown    */new Color(139, 69, 19),
		/*dark khaki      */new Color(189,183,107),	
		/*light gray      */new Color(192,192,192),	
		/*dark gray       */new Color(128,128,128),	
	};
	private final static String noGoalString = "(none)";
	
	private ArrayList<ImageIcon> goalIcons;
	private ImageIcon colorChoiceIcon;
	private ArrayList<ImageIcon> goalColorChoiceIcons, taskColorChoiceIcons;//used in changing item colors during edit mode
	private ArrayList<JTextField> taskNameFields, goalNameFields;//used in changing item names during edit mode
	
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
        setSize(new Dimension(800,610));
        setResizable(false);//lock window size
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
		goalPanel.setBounds(525, 5, 265, 550);
		goalPanel.setVisible(true);
		add(goalPanel);
		
		goalIcons = new ArrayList<ImageIcon>();
		availableColors = new ArrayList<Color>();
		for(Color c : goalColors)//all colors initially available
			availableColors.add(c);
		ColorChoicePanel colorChoicePanel = new ColorChoicePanel();
		colorChoiceIcon = createImageIcon(colorChoicePanel, 25, 25);
		
		calendarPanel.ping();//get initially selected date to display (from calendarPanel)
		
		//configure newTaskButton
		newTaskButton = new JButton("New Task");
		newTaskButton.setBounds(310, 170, 104, 50);
		newTaskButton.setVisible(true);
		newTaskButton.addActionListener(
			new ActionListener(){//(anonymous inner class) create newTask MessageDialog
				JTextField taskNameField = new JTextField();
				JLabel taskNameFieldLabel = new JLabel("Task name: ");
				JLabel goalBoxLabel = new JLabel("Select related goal: ");
				JComboBox goalBox;
				public void actionPerformed(ActionEvent event) {
					goalBox = createGoalComboBox();//get available related-goal choices
					JComponent[] inputs = new JComponent[]{//assemble optionDialog prompting user for task name/related goal
						taskNameFieldLabel, taskNameField, goalBoxLabel, goalBox};
					String[] options = {"Add Task", "Cancel"};
					int response = JOptionPane.showOptionDialog(null, inputs, "Create new task", JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.PLAIN_MESSAGE, null, options, "Add Task");
					if(response==0 ){//if user clicked "Add Task"
						if(taskNameField.getText().equals(""))
							JOptionPane.showMessageDialog(null,"Task must have a name");
						else
							addNewTask(taskNameField.getText(), (String)goalBox.getSelectedItem());
					}
					taskNameField.setText("");//clear text field
				}
			} // end anonymous inner class
		); // end call to addActionListener
		add(newTaskButton);
		
		//configure newGoalButton
		newGoalButton = new JButton("New Goal");
		newGoalButton.setBounds(416, 170, 104, 50);
		newGoalButton.setVisible(true);
		newGoalButton.addActionListener(
			new ActionListener(){//(anonymous inner class) create newGoal MessageDialog
				JTextField goalNameField = new JTextField();
				JLabel goalNameFieldLabel = new JLabel("Goal Name");
				JLabel colorBoxLabel = new JLabel("Select Color");
				JComboBox colorBox;
				public void actionPerformed(ActionEvent event) {
					colorBox = createColorChoiceComboBox();
					JComponent[] inputs = new JComponent[]{//assemble optionDialog prompting user for goal name/color
						goalNameFieldLabel, goalNameField,
						colorBoxLabel, colorBox};
					String[] options = {"Add Goal", "Cancel"};
					int response = JOptionPane.showOptionDialog(null, inputs, "Create new goal", JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.PLAIN_MESSAGE, null, options, "Add Goal");
					if(response==0){//if user clicked "Add Goal"
						if(goalNameField.getText().equals("")||goalNameField.getText().equals(noGoalString))
							JOptionPane.showMessageDialog(null,"Invalid goal name");
						else if(planner.getGoalNames().contains(goalNameField.getText()))
							JOptionPane.showMessageDialog(null,"Goal with chosen name already exists");
						else
							addNewGoal(goalNameField.getText(), availableColors.get(colorBox.getSelectedIndex()));
					}
					goalNameField.setText("");//clear text field
				}
			} // end anonymous inner class
		); // end call to addActionListener
		add(newGoalButton);
		
		//configure editTasksButton
		editTasksButton = new JButton("Edit Tasks");
		editTasksButton.setBounds(310, 222, 210, 40);
		editTasksButton.setVisible(true);
		editTasksButton.addActionListener(
				new ActionListener(){// anonymous inner class
					public void actionPerformed(ActionEvent event) {
						JComponent[] inputs = new JComponent[]{
						new JLabel(colorChoiceIcon)};
						JOptionPane.showMessageDialog(null, inputs, "Edit tasks", JOptionPane.PLAIN_MESSAGE);
					}
				} // end anonymous inner class
			);
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
	
	//add new task to planner
	public void addNewTask(String taskName, String goalName){
		if(planner.getGoalNames().contains(goalName))
			planner.addNewTask(guiDate, taskName, planner.getGoals().get(planner.getGoalNames().indexOf(goalName)));
		else
			planner.addNewTask(guiDate, taskName);
		calendarPanel.ping();//update displayed tasks for currently selected calendar date
	}
	
	
	//add new goal to planner
	public void addNewGoal(String n, Color c){
		planner.addNewGoal(n, c);
		goalPanel.addItem(n, c);
		availableColors.remove(c);//prevent future goals from using same color
		goalIcons.add(createColorIcon(20, 20, c));
		goalPanel.repaint();//update displayed goals
	}
	
	//remove goal at specified index (from planner, goalIcons, & goalBox)
	public void removeGoal(int index){
		availableColors.add(planner.getGoal(index).getColor());//goal color can now be used again
		goalIcons.remove(index);
		goalPanel.removeItem(index);
		planner.removeGoal(index);
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
	
	//turns on/off edit mode for tasks/goals (specified by "mode" string)
	private void setEditMode(String mode, boolean enabled){
		//todo: case statement for tasks/goals edit mode enabled/disabled
		
	}
	
	//returns an ImageIcon of the specified width, height, and color
	private ImageIcon createColorIcon(int width, int height, Color color){
		JPanel colorSquare = new JPanel();
		colorSquare.setBackground(color);
		return createImageIcon(colorSquare, width, height);
	}
	//returns an ImageIcon generated from specified JPanel
	private ImageIcon createImageIcon(JPanel panel, int width, int height){
		panel.setBounds(0, 0, width, height);
		panel.doLayout();
		panel.validate();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = img.createGraphics();
		panel.paint(g);
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
		ArrayList<String> items = planner.getGoalNames();
		items.add(0,noGoalString);//add no goal option to front of item list
		JComboBox goalBox = new JComboBox(items.toArray());
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
			if(value.equals(noGoalString))
				setIcon(null);
			else{
				try{setIcon(goalIcons.get(planner.getGoalNames().indexOf(value)));}
				catch(ArrayIndexOutOfBoundsException e){}
			}
			setText((String)value);
			setFont(list.getFont());
			return this;
		}	
	}
	
	//(inner class, component) rainbow color-choice panel (used in goal/task edit mode)
	private class ColorChoicePanel extends JPanel implements MouseListener{
		JLabel textLabel;
		int width, height;
		public ColorChoicePanel(){
			super();
			textLabel = new JLabel("?",SwingConstants.CENTER);//set label text + horizontal alignment
			textLabel.setVerticalAlignment(SwingConstants.CENTER);
			textLabel.setFont(textLabel.getFont().deriveFont((float)26));//set fontsize to 26
			textLabel.setForeground(Color.BLACK);
			add(textLabel);
			setVisible(true);
		}
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			width = getWidth();
			height = getHeight();
			textLabel.setBounds(-width, -height, width*3, height*3);
			textLabel.setVisible(true);
			g.setColor(Color.RED);
			g.fillPolygon(new int[]{0, width/2, width/2}, new int[]{0, 0, height/2}, 3);
			g.setColor(Color.ORANGE);
			g.fillPolygon(new int[]{width/2, width, width/2}, new int[]{0, 0, height/2}, 3);
			g.setColor(Color.YELLOW);
			g.fillPolygon(new int[]{width/2, width, width}, new int[]{height/2, 0, height/2}, 3);
			g.setColor(Color.GREEN);
			g.fillPolygon(new int[]{width/2, width, width}, new int[]{height/2, height/2, height}, 3);
			g.setColor(new Color(  0,128,128));//"teal"
			g.fillPolygon(new int[]{width/2, width, width/2}, new int[]{height/2, height, height}, 3);
			g.setColor(new Color(30,144,255));//"dodger blue"
			g.fillPolygon(new int[]{0, width/2, width/2}, new int[]{height, height/2, height}, 3);
			g.setColor(new Color(128,  0,128));//"purple"
			g.fillPolygon(new int[]{0, 0, width/2}, new int[]{height, height/2, height/2}, 3);
			g.setColor(new Color(255,105,180));//"hot pink"
			g.fillPolygon(new int[]{0, width/2, 0}, new int[]{0, height/2, height/2}, 3);
		}
		
		//(implemented) MouseEvents (listener added in constructor)
		//todo: toggleable mouselistener for task/goal edit
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
	}
}
