import java.awt.Color;


public class GkDriver {
	
	public static void main(String[]args){
		GkGUI app1 = new GkGUI();
		
		Planner planner = new Planner("New Planner");
		
		
//********test code************************************************************
		int testYMD = 20121213;
		planner.addNewTask(testYMD, "Test TaskPanel update on calendar events");
		planner.addNewTask(testYMD, "Do victory dance");
		
		planner.addNewGoal("Chores", Color.cyan);
		planner.addNewGoal("Learn Chinese", Color.red);
		System.out.println("Goal: " + planner.getGoal(0).getName() + " added on " + planner.getGoal(0).getStartDate());
		planner.addNewTask(testYMD+1, "Clean up after victory party", planner.getGoal(0));
		planner.addNewTask(testYMD+1, "Nurse hangover", planner.getGoal(0));
		app1.setPlanner(planner);
	//********end of test code************************************************************
		
		app1.execute();
		
		
	}
	
}
