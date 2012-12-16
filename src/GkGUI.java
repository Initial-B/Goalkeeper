//Brady Whytock
//ITP 220 - final project

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.filechooser.*;

import calendarpanel.GkCalendarPanel;


import taskpanel.*;

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
	final static String noGoalString = "(none)";//default marker for tasks/colorpanels with no related goals (must be nonempty)
	
	private ArrayList<Color> availableColors;
	private ArrayList<ImageIcon> goalIcons;
	
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenuItem saveMenuItem, saveAsMenuItem, openMenuItem, newMenuItem, quitMenuItem;
	private File lastSaveFile;
	
	private TaskPanel taskPanel;
	private JLabel nameLabel;//displays planner name
	private JLabel dateLabel;//displays currently selected date
	private GkCalendarPanel calendarPanel;
	private GoalPanel goalPanel;
	private EditOverlay editOverlay;
	
	private JButton newTaskButton, newGoalButton, editTasksButton, editGoalsButton,
		saveChangesButton, goalTrackingButton;
	
	private Planner planner;
	private int guiDate;//yyyymmdd of currently displayed planner date
	
	private boolean taskEditEnabled, goalEditEnabled;
	
	public GkGUI(){
		//configure main GUI window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JLayeredPane contentPane = new JLayeredPane();
		contentPane.setLayout(null);// enable explicit positioning of GUI components
        contentPane.setBackground( Color.lightGray);
        setContentPane(contentPane);
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
		
		//configure newMenuItem
		newMenuItem = new JMenuItem("New");
		newMenuItem.setMnemonic(KeyEvent.VK_N);
		fileMenu.add(newMenuItem);
		fileMenu.addSeparator();
		newMenuItem.addActionListener(
			new ActionListener(){ // anonymous inner class
				public void actionPerformed(ActionEvent event){
					newPlanner();
				}
		});
		
		//configure openMenuItem
		openMenuItem = new JMenuItem("Open...");
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		fileMenu.add(openMenuItem);
		openMenuItem.addActionListener(
			new ActionListener(){ // anonymous inner class
				public void actionPerformed(ActionEvent event){
					loadPlanner();
				}
		});
		
		//configure saveMenuItem
		saveMenuItem = new JMenuItem("Save");
		saveMenuItem.setMnemonic(KeyEvent.VK_S);
		fileMenu.add(saveMenuItem);
		saveMenuItem.addActionListener(
			new ActionListener(){ // anonymous inner class
				public void actionPerformed(ActionEvent event){
					savePlanner();
				}
		});
		
		//configure saveAsMenuItem
		saveAsMenuItem = new JMenuItem("Save As...");
		fileMenu.add(saveAsMenuItem);
		fileMenu.addSeparator();
		saveAsMenuItem.addActionListener(
			new ActionListener(){ // anonymous inner class
				public void actionPerformed(ActionEvent event){
					savePlannerAs();
				}
		});
		
		//configure quitMenuItem
		quitMenuItem = new JMenuItem("Exit");
		fileMenu.add(quitMenuItem);
		quitMenuItem.setMnemonic(KeyEvent.VK_X);
		quitMenuItem.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent event){
					System.exit(0);
				}
		});
		
        
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
		
		//instantiate misc. variables
		goalIcons = new ArrayList<ImageIcon>();
		availableColors = new ArrayList<Color>();
		for(Color c : goalColors)//all colors initially available
			availableColors.add(c);
		taskEditEnabled = false;
		goalEditEnabled = false;
		calendarPanel.ping();//get initially selected date to display (from calendarPanel)
		
		//configure newTaskButton
		newTaskButton = new JButton("New Task");
		newTaskButton.setBounds(310, 210, 104, 50);
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
		}); // end call to addActionListener
		add(newTaskButton);
		
		//configure newGoalButton
		newGoalButton = new JButton("New Goal");
		newGoalButton.setBounds(416, 210, 104, 50);
		newGoalButton.setVisible(true);
		newGoalButton.addActionListener(new ActionListener(){//(anonymous inner class) create newGoal MessageDialog
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
		}); // end call to addActionListener
		add(newGoalButton);
		
		//configure editTasksButton
		editTasksButton = new JButton("Edit Tasks");
		editTasksButton.setBounds(310, 300, 210, 40);
		editTasksButton.setVisible(true);
		editTasksButton.addActionListener(new ActionListener(){// anonymous inner class
			public void actionPerformed(ActionEvent event) {
				if(goalEditEnabled){
					editOverlay.finalizeEdits();
					goalEditEnabled = false;
					updateGoals();
				}
				if(!taskEditEnabled){
					editOverlay.addNewTaskEditOverlay(taskPanel);
					taskEditEnabled = true;
				}else{
					editOverlay.finalizeEdits();
					taskEditEnabled = false;
					calendarPanel.ping();
				}
			}
		});
		add(editTasksButton);
		
		//configure editGoalsButton
		editGoalsButton = new JButton("Edit Goals");
		editGoalsButton.setBounds(310, 380, 210, 40);
		editGoalsButton.setVisible(true);
		editGoalsButton.addActionListener(new ActionListener(){// anonymous inner class
			public void actionPerformed(ActionEvent event) {
				if(taskEditEnabled){
					editOverlay.finalizeEdits();
					taskEditEnabled = false;
					calendarPanel.ping();
				}
				if(!goalEditEnabled){
					editOverlay.addNewGoalEditOverlay(goalPanel);
					goalEditEnabled = true;
				}else{
					editOverlay.finalizeEdits();
					goalEditEnabled = false;
					updateGoals();
				}
			}
		});
		add(editGoalsButton);
		
		//configure saveChangesButton
		saveChangesButton = new JButton("Save Changes");
		saveChangesButton.setBounds(310, 460, 210, 40);
		saveChangesButton.setVisible(true);
		saveChangesButton.addActionListener(new ActionListener(){//(anonymous inner class) create newTask MessageDialog
			public void actionPerformed(ActionEvent event){
				if(taskEditEnabled||goalEditEnabled){
					editOverlay.finalizeEdits();
					calendarPanel.ping();
					updateGoals();
				}
				savePlanner();
			}
		});
		add(saveChangesButton);
	}
	
	//launch frame components
	public void execute(){
		//startup dialog
		String[] buttons = {"New Planner", "Load Planner", "Exit"}; 
		int response = JOptionPane.showOptionDialog(null, "Welcome to Goalkeeper!", "Goalkeeper",
		        JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE, null, buttons, null);
		if(response==2)
			System.exit(0);
		else if(response==1){
			if(!loadPlanner())//if user cancels load planner dialog
				execute();//restart goalkeeper
		}else{
			newPlanner();
		}
		setVisible(true);
		for(Component c : getComponents())
			c.setVisible(true);
	}
	
	//conventional access methods
	public ArrayList<Color> getAvailableColors(){return availableColors;}
	public void setAvailableColors(ArrayList<Color> colors){
		availableColors.clear();
		for(Color c : colors)
			availableColors.add(c);
	}
	public String getNoGoalString(){return noGoalString;}
	public int getGuiDate(){return guiDate;}
	public Planner getPlanner(){return planner;}
	
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
		updateGoals();
		goalPanel.repaint();//update displayed goals
	}
	
	//remove goal at specified index (from planner, goalIcons, & goalBox)
	public void removeGoal(int index){
		planner.removeGoal(index);
		updateGoals();
	}
	
	
	//set planner & update gui
	public void setPlanner(Planner p){
		planner = p;
		calendarPanel.ping();
		nameLabel.setText(planner.getName());
		editOverlay = new EditOverlay(this);
		updateGoals();
	}
	
	//update goalPanel/availableColors to reflect goals in planner
	public void updateGoals(){
		for(int x = goalPanel.getItems().size()-1; x >= 0;x--)//remove all items from goalPanel
			goalPanel.removeItem(x);
		availableColors = new ArrayList<Color>(Arrays.asList(goalColors));
		goalIcons.clear();
		for(Planner.Goal goal : planner.getGoals()){
			goalPanel.addItem(goal.getName(), goal.getColor());//add to goalPanel all goals in planner
			availableColors.remove(goal.getColor());//and make its color unavailable
			goalIcons.add(createColorIcon(20,20,goal.getColor()));//add icon of goal color to goalIcons 
		}
	}
	
	//update dateLabel
	private void updateDate(){dateLabel.setText(calendarPanel.getDateString());}
	

	//actionlistener to respond to date selection event from calendarpanel
	public class CalendarListener implements ActionListener{
		public void actionPerformed(ActionEvent e) {
			updateDate();
			guiDate = Integer.valueOf(e.getActionCommand());
			for(int x = 0;x < taskPanel.getItems().size();x++){
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
		return createColorChoiceComboBox(availableColors);
	}
	//returns a JComboBox featuring the specified colors
	JComboBox createColorChoiceComboBox(ArrayList<Color> colors){
		ImageIcon[]colorIcons = new ImageIcon[colors.size()];
		for(int x = 0;x < colorIcons.length;x++)
			colorIcons[x] = createColorIcon(200, 20, colors.get(x));
		JComboBox colorBox = new JComboBox(colorIcons);
		return colorBox;
	}
	
	//returns a JComboBox featuring the current goals
	JComboBox createGoalComboBox(){
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
	
	//new planner dialog
	public void newPlanner(){
		String name = JOptionPane.showInputDialog(this, "Enter name:", 
				"New Planner",JOptionPane.QUESTION_MESSAGE);
		if(name==null)
			execute();
		else if(name.equals("")){
			JOptionPane.showMessageDialog(this, "Name must be at least 1 character long");
			execute();
		}
		Planner newPlanner = new Planner(name);
		setPlanner(newPlanner);
	}
	
	//save planner under most recent filename
	public void savePlanner(){
		try {
			if(lastSaveFile!=null){
				FileOutputStream saveFile = new FileOutputStream(lastSaveFile);
				ObjectOutputStream save = new ObjectOutputStream(saveFile);
				save.writeObject(planner);
				save.close();
			}
			else//if no lastSaveFile, prompt saveDialog
				savePlannerAs();
		} catch (Exception e) {e.printStackTrace();}
	}
	
	//save planner using saveDialog
	public void savePlannerAs(){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Goalkeeper save file (.sav)", "sav"));
		try{chooser.setCurrentDirectory(lastSaveFile.getParentFile());}
			catch(Exception e){}
		int returnVal = chooser.showSaveDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION){
			try{
				File saveFile = chooser.getSelectedFile().getAbsoluteFile();
				FileOutputStream saveStream;
				if(!saveFile.getAbsolutePath().endsWith(".sav"))//append file extension if missing
					saveFile = new File(saveFile + ".sav");
				saveStream = new FileOutputStream(saveFile);
				ObjectOutputStream save = new ObjectOutputStream(saveStream);
				lastSaveFile = saveFile;
				setTitle("Goalkeeper - " + lastSaveFile.getName());
				save.writeObject(planner);
				save.close();
			}catch(Exception e){e.printStackTrace();}
		}
	}
	
	//load a planner savefile into GkGUI
	public boolean loadPlanner(){
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Goalkeeper save file (.sav)", "sav"));
		try{chooser.setCurrentDirectory(lastSaveFile.getParentFile());}
			catch(Exception e){}
		int returnVal = chooser.showOpenDialog(this);
	    if(returnVal == JFileChooser.APPROVE_OPTION){
	    	try{
	    		FileInputStream saveFile = new FileInputStream(chooser.getSelectedFile());
	    		ObjectInputStream save = new ObjectInputStream(saveFile);
	    		Planner loadedPlanner = (Planner)save.readObject();
	    		lastSaveFile = chooser.getSelectedFile().getAbsoluteFile();
	    		setTitle("Goalkeeper - " + lastSaveFile.getName()); 
	    		setPlanner(loadedPlanner);
	    		save.close();
	    		return true;
	    	}catch (Exception e){
	    		e.printStackTrace();
	    		return true;
	    	}
	    }
	    else 
	    	return false;
	}
}
