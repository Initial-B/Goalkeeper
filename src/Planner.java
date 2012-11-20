import java.awt.*;
import java.util.*;
public class Planner {

	private String owner;
	private String filepath;
	private TreeMap<Integer, Day> schedule;
	private ArrayList<Goal> goals;
	
	public Planner(){
		schedule = new TreeMap<Integer, Day>();
	}
	
	
	public Day getDay(int ymd){
		if(schedule.containsKey(ymd))
			return schedule.get(ymd);
		else{
			Day retDay = new Day(ymd);
			schedule.put(ymd,retDay);
			return retDay;
		}
	}
	
	
	
	private class Day{
		private int hashDate;//date as int YYYYMMDD 
		private ArrayList<Task> tasks;
		
		public Day(int ymd){
			hashDate = ymd;
			tasks = new ArrayList<Task>();
		}
		public void addTask(String n){
			Task task = new Task(n);
			tasks.add(task);
		}
	}
	
	private class Task{
		private String name;
		private Goal relatedGoal;
		private boolean completed;
		
		public Task(String n){
			name = n;
			completed = false;
		}
		public void setGoal(Goal g){relatedGoal = g;}
	}
	
	private class Goal{
		private Color color;
		private String name;
		private Date dateAdded;
		private Date dateRemoved;
	}
}
