import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLayeredPane;


public class EditOverlay{
	private JPanel goalOverlay;
	private TaskEditOverlay taskEditor;
	private GoalEditOverlay goalEditor;
	private GkGUI goalkeeper;
	private String noGoalString;
	private Planner planner;
	
	//constructor
	public EditOverlay(GkGUI gkGUI){
		goalkeeper = gkGUI;
		planner = gkGUI.getPlanner();
		noGoalString = gkGUI.getNoGoalString();
	}
	
	//(inner class, component) JPanel overlay for editing items in a TaskPanel
	private class TaskEditOverlay extends JPanel{
		ArrayList<JTextField> taskNameFields;
		ArrayList<ColorChoicePanel> taskColorPanels;
		
		public TaskEditOverlay(){
			setOpaque(false);//panel background is transparent
			setLayout(null);
			taskNameFields = new ArrayList<JTextField>();
			taskColorPanels = new ArrayList<ColorChoicePanel>();
		}
	}
	//(inner class, component) JPanel overlay for editing items in a GoalPanel
	private class GoalEditOverlay extends JPanel{
		ArrayList<JTextField> goalNameFields;
		ArrayList<ColorChoicePanel> goalColorPanels;
		ArrayList<Color> tempAvailableColors;//editOverlay modifies temporary color list until changes are finalized
		
		public GoalEditOverlay(){
			setOpaque(false);//panel background is transparent
			setLayout(null);
			tempAvailableColors = new ArrayList<Color>();
			for(Color c : goalkeeper.getAvailableColors())
				tempAvailableColors.add(c);
			goalNameFields = new ArrayList<JTextField>();
			goalColorPanels = new ArrayList<ColorChoicePanel>();
		}
	}
	
	//create + add a custom edit overlay component to the specified taskPanel,
	//   based on the tasks of the specified date in the planner
	public void addNewTaskEditOverlay(taskpanel.TaskPanel taskPanel){
		taskEditor = new TaskEditOverlay();
		taskEditor.setBounds(taskPanel.getBounds());//set size of overlay to match taskPanel
		int itemSpacing = taskPanel.getItemSpacing();
		int dateYMD = goalkeeper.getGuiDate();
		for(int y = 0;y < planner.getTasks(dateYMD).size();y++){
			//configure colorPanels
			ColorChoicePanel colorPanel = new ColorChoicePanel(y);
			colorPanel.setBounds(2, 2 + (y*itemSpacing), itemSpacing - 4, itemSpacing - 4);
			colorPanel.setGoalName(planner.getTask(dateYMD, y).getGoal().getName());
			colorPanel.setColor(planner.getTask(dateYMD, y).getGoal().getColor());
			colorPanel.setVisible(true);
			colorPanel.addMouseListener(
				new MouseListener(){
					JLabel goalBoxLabel;
					JComboBox goalBox = goalkeeper.createGoalComboBox();
					public void mousePressed(MouseEvent e) {
						ColorChoicePanel colorPanel = (ColorChoicePanel)e.getSource();
						int index = colorPanel.getIndex();
						goalBoxLabel= new JLabel("Select related goal for \"" + 
								taskEditor.taskNameFields.get(index).getText() + "\"");
						//assemble optionDialog prompting user for related goal
						JComponent[] inputs = new JComponent[]{goalBoxLabel, goalBox};
						String[] options = {"Ok", "Cancel"};
						int response = JOptionPane.showOptionDialog(null, inputs, "Edit task", 
							JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Ok");
						if(response==0){//if user clicked "Ok"
							colorPanel.setGoalName(((String)goalBox.getSelectedItem()));
							if(colorPanel.getGoalName().equals(noGoalString))//if no goal selected
								colorPanel.setColor(Color.WHITE);
							else
								colorPanel.setColor(planner.getGoalColors().get(goalBox.getSelectedIndex() - 1));
							colorPanel.newGoalPicked = true;
							colorPanel.repaint();
						}
					}
					public void mouseClicked(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
			});//end of call to addMouseListener
			taskEditor.taskColorPanels.add(colorPanel);
			taskEditor.add(colorPanel);
			//configure taskNameFields
			JTextField taskNameField = new JTextField();
			taskNameField.setBounds(taskPanel.getItems().get(y).getBounds());//dimensions are same as task JLabels
			taskNameField.setText(planner.getTask(dateYMD, y).getName());
			taskNameField.setVisible(true);
			taskEditor.taskNameFields.add(taskNameField);
			taskEditor.add(taskNameField);
		}
		taskEditor.setVisible(true);
		//add taskEditor to goalkeeper one layer above taskPanel
		goalkeeper.add(taskEditor, JLayeredPane.getLayer(taskPanel) + 1);
	}
	
	public void addNewGoalEditOverlay(goalpanel.GoalPanel goalPanel){
		goalEditor = new GoalEditOverlay();
		goalEditor.setBounds(goalPanel.getBounds());//set size of overlay to match taskPanel
		int itemSpacing = goalPanel.getItemSpacing();
		for(int y = 0;y < planner.getGoals().size();y++){
			//configure goalColorPanels
			ColorChoicePanel colorPanel = new ColorChoicePanel(y);
			colorPanel.setBounds(2, 2 + ((y+1)*itemSpacing), itemSpacing - 4, itemSpacing - 4);
			colorPanel.setGoalName(planner.getGoals().get(y).getName());
			colorPanel.setColor(planner.getGoalColors().get(y));
			colorPanel.setVisible(true);
			colorPanel.addMouseListener(
				new MouseListener(){
					JLabel colorBoxLabel;
					JComboBox colorBox;
					public void mousePressed(MouseEvent e) {
						if(goalEditor.tempAvailableColors.size() > 0){
							colorBox = goalkeeper.createColorChoiceComboBox(goalEditor.tempAvailableColors);
							ColorChoicePanel colorPanel = (ColorChoicePanel)e.getSource();
							int index = colorPanel.getIndex();
							colorBoxLabel= new JLabel("Select new color for \"" + 
								goalEditor.goalNameFields.get(index).getText() + "\"");
							//assemble optionDialog prompting user for related goal
							JComponent[] inputs = new JComponent[]{colorBoxLabel, colorBox};
							String[] options = {"Ok", "Cancel"};
							int response = JOptionPane.showOptionDialog(null, inputs, "Edit goal", 
								JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, "Ok");
							if(response==0){//if user clicked "Ok"
								goalEditor.tempAvailableColors.add(colorPanel.getColor());//make old goal color available
								colorPanel.setColor(goalEditor.tempAvailableColors.get(colorBox.getSelectedIndex()));
								goalEditor.tempAvailableColors.remove(colorBox.getSelectedIndex());//make new goal color unavailable
								colorPanel.newGoalPicked = true;
								colorPanel.repaint();
							}
						}
						else
							JOptionPane.showMessageDialog(null,"No available colors remaining");
					}
					public void mouseClicked(MouseEvent e) {}
					public void mouseEntered(MouseEvent e) {}
					public void mouseExited(MouseEvent e) {}
					public void mouseReleased(MouseEvent e) {}
			});//end of call to addMouseListener			
			goalEditor.goalColorPanels.add(colorPanel);
			goalEditor.add(colorPanel);
			//configure goalNameFields
			JTextField goalNameField = new JTextField();
			goalNameField.setBounds(goalPanel.getItems().get(y).getBounds());//dimensions are same as goal JLabels
			goalNameField.setText(planner.getGoal(y).getName());
			goalNameField.setVisible(true);
			goalEditor.goalNameFields.add(goalNameField);
			goalEditor.add(goalNameField);
		}
		goalEditor.setVisible(true);
		//add goalEditor to goalkeeper one layer above goalPanel
		goalkeeper.add(goalEditor, JLayeredPane.getLayer(goalPanel) + 1);
	}
	
	//commit changes to planner + remove overlays
	public void finalizeEdits(){
		//commit task changes if taskEditor is in use
		try{
			if(taskEditor.isVisible() && confirmEdits(taskEditor)){
				int guiDate = goalkeeper.getGuiDate();
				//iterate through tasks in reverse order
				for(int x = planner.getTasks(guiDate).size()-1;x >= 0;x--){
					ColorChoicePanel colorPanel = taskEditor.taskColorPanels.get(x);
					if(colorPanel.newGoalPicked){//if colorPanel was used to select a related goal
						if(!colorPanel.goalName.equals(noGoalString))//if goal choice is a real goal
							planner.getTask(guiDate,x).setGoal(planner.getGoal(planner.getGoalNames().indexOf(colorPanel.goalName)));
						else //else set goal to null
							planner.getTask(guiDate,x).removeGoal();
					}
					JTextField taskNameField = taskEditor.taskNameFields.get(x);
					//remove task if name has been deleted
					if(taskNameField.getText().equals(""))
						planner.removeTask(guiDate, x);
					else
						planner.getTask(guiDate,x).setName(taskNameField.getText());
				}
			}
			//remove task editOverlay
			taskEditor.setVisible(false);
			goalkeeper.remove(taskEditor);
		}catch(NullPointerException e){}
		//commit goal changes if goalEditor is in use
		try{
			if(goalEditor.isVisible() && confirmEdits(goalEditor)){
				for(int x = planner.getGoals().size()-1;x >= 0;x--){
					ColorChoicePanel colorPanel = goalEditor.goalColorPanels.get(x);
					if(colorPanel.newGoalPicked)//if colorPanel was used to select a new color
						planner.getGoal(x).setColor(colorPanel.getColor());
					JTextField goalNameField = goalEditor.goalNameFields.get(x);
					if(goalNameField.getText().equals(noGoalString))
						planner.getGoal(x).setName("Goal " + (x+1));
					else if(!goalNameField.getText().equals(""))
						planner.getGoal(x).setName(goalNameField.getText());
					else{//if goal name is blank, make its color available (if it is not already) + remove goal
						if(!goalEditor.tempAvailableColors.contains(planner.getGoalColors().get(x)))
							goalEditor.tempAvailableColors.add(planner.getGoalColors().get(x));
						planner.removeGoal(x);
					}
				 }
				 goalkeeper.setAvailableColors(goalEditor.tempAvailableColors);//update available colors
				 goalkeeper.updateGoals();//update goalPanel in GkGUI
				 //remove task editOverlay
			}
			 goalEditor.setVisible(false);
			 goalkeeper.remove(goalEditor);
		}catch(NullPointerException e){}
	}
	
	//dialog for confirming task edits
	public boolean confirmEdits(TaskEditOverlay tEO){
		boolean editsMade = false;
		//iterate through editor components, checking if any changes made
		for(int x = 0; x < tEO.taskColorPanels.size();x++){
			ColorChoicePanel colorPanel = tEO.taskColorPanels.get(x);
			if((colorPanel.newGoalPicked 
			 && !colorPanel.color.equals(planner.getTask(goalkeeper.getGuiDate(),x).getGoal().getColor()))
			 || !tEO.taskNameFields.get(x).getText().equals(planner.getTask(goalkeeper.getGuiDate(),x).getName()))
			 	editsMade = true;
		}
		if(!editsMade)
			return true;
		else{
			String deletedTasks = "";
			for(int x = 0;x < tEO.taskNameFields.size();x++){
				if(tEO.taskNameFields.get(x).getText().equals(""))
					deletedTasks += ("\t\t-" + planner.getTask(goalkeeper.getGuiDate(), x).getName() + "\n");
			}
			if(!deletedTasks.equals(""))//if any tasks have been deleted
				deletedTasks = "\n\tThe following task(s) will be deleted:\n" + deletedTasks;
			int response = JOptionPane.showOptionDialog(null, "Apply changes to tasks?" + deletedTasks,
				"Task Edit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[]{"Apply changes", "Discard changes"}, "Apply changes");
			return (response==0);
		}
	}
	
	//dialog for confirming goal edits
	public boolean confirmEdits(GoalEditOverlay gEO){
		boolean editsMade = false;
		//iterate through editor components, checking if any changes made
		for(int x = 0; x < gEO.goalColorPanels.size();x++){
			ColorChoicePanel colorPanel = gEO.goalColorPanels.get(x);
			if((colorPanel.newGoalPicked
			 && !colorPanel.color.equals(planner.getGoalColors().get(x))
			 || !gEO.goalNameFields.get(x).getText().equals(planner.getGoalNames().get(x))))
				editsMade = true;
		}
		if(!editsMade)
			return true;
		else{
			String deletedGoals = "";
			for(int x = 0;x < gEO.goalNameFields.size();x++){
				if(gEO.goalNameFields.get(x).getText().equals(""))
					deletedGoals += ("\t\t-" + planner.getGoalNames().get(x) + "\n");
			}
			if(!deletedGoals.equals(""))//if any goals have been deleted
				deletedGoals = "\n\tThe following goal(s) will be deleted:\n" + deletedGoals;
			int response = JOptionPane.showOptionDialog(null, "Apply changes to goals?" + deletedGoals,
				"Goal Edit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
				new String[]{"Apply changes", "Discard changes"}, "Apply changes");
			return (response==0);
		}
	}
}
