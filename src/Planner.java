import java.awt.*;
import java.util.*;
public class Planner {

	private String owner;
	private String filepath;
	private TreeMap<Integer, Day> schedule;
	private ArrayList<Goal> goals;
	
	
	private class Day{
		private Date date;
		private ArrayList<Task> tasks;
		
	}
	
	private class Task{
		private String name;
		private Goal relatedGoal;
		private boolean completed;
	}
	
	private class Goal{
		private Color color;
		private String name;
		private Date dateAdded;
		private Date dateRemoved;
	}
}
