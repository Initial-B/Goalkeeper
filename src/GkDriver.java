import java.awt.Color;


public class GkDriver {
	
	public static void main(String[]args){
		GkGUI app1 = new GkGUI();
		
		Planner planner = new Planner("Brady Whytock");
		app1.setPlanner(planner);
		
//********test code************************************************************
		int testYMD = 20121123;
		planner.addNewTask(testYMD, "Test TaskPanel update on calendar events");
		planner.addNewTask(testYMD, "Do victory dance");
		
		app1.addNewGoal("Chores", Color.cyan);
		app1.addNewGoal("Learn Chinese", Color.red);
		System.out.println("Goal: " + planner.getGoal(0).getName() + " added on " + planner.getGoal(0).getStartDate());
		planner.addNewTask(testYMD+1, "Clean up after victory party", planner.getGoal(0));
		planner.addNewTask(testYMD+1, "Nurse hangover", planner.getGoal(0));
//********end of test code************************************************************
		
		app1.execute();
		
		
	}
	
}
