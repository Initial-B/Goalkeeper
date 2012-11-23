
public class GkDriver {
	
	public static void main(String[]args){
		GkGUI app1 = new GkGUI();
		
		Planner planner = new Planner();
		int testYMD = 20121123;
		planner.addTask(testYMD, "Test TaskPanel update on calendar events");
		planner.addTask(testYMD, "Do victory dance");
		app1.setPlanner(planner);
		
		
		app1.execute();
	}
	
}
