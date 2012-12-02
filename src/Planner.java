import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
public class Planner {
	
	final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//default dateFormat for calendar dates
	
	private String owner;
	private String filepath;
	private TreeMap<Integer, Day> schedule;
	private ArrayList<Goal> goals;//list of current & past goals
	private Calendar calendar;//calendar for setting Goal start/end dates
	
	
	public Planner(){
		schedule = new TreeMap<Integer, Day>();
		goals = new ArrayList<Goal>();
	}
	
	//return Day with associated date, create if DNE
	public Day getDay(int ymd){
		if(schedule.containsKey(ymd))
			return schedule.get(ymd);
		else{
			Day retDay = new Day(ymd);
			schedule.put(ymd,retDay);
			return retDay;
		}
	}
	//create and add a task with no related goal
	public void addNewTask(int ymd, String n){getDay(ymd).addTask(new Task(n));}
	
	//create and add a task with a related goal
	public void addNewTask(int ymd, String n, Goal g){getDay(ymd).addTask(new Task(n, g));}
	
	//create and add an active goal with specified color
	public void addNewGoal(String n, Color c){addGoal(new Goal(n, c));}
	
	//add an active goal to goals
	public void addGoal(Goal g){
		calendar = Calendar.getInstance();
		g.dateAdded = calendar.getTime();
		goals.add(g);
	}
	
	//deactivate goal
	public void removeGoal(Goal g){
		calendar = Calendar.getInstance();
		g.dateAdded = calendar.getTime();
		g.active = false;
	}
	
	//returns an arraylist of tasks of the specified date
	public ArrayList<Task> getTasks(int ymd){return getDay(ymd).tasks;}
	
	//return task at specified index of specified date
	public Task getTask(int ymd, int index){return getDay(ymd).tasks.get(index);}
	
	//conventional access methods
	public Goal getGoal(int index){return goals.get(index);}
	
	
//(inner class) Day item
	public class Day{
		private int hashDate;//date as int YYYYMMDD 
		private ArrayList<Task> tasks;
		
		public Day(int ymd){
			hashDate = ymd;
			tasks = new ArrayList<Task>();
		}
		//add specified task
		public void addTask(Task t){tasks.add(t);}
	}

//(inner class) Task item	
	public class Task{
		private String name;
		private Goal relatedGoal;
		private boolean completed;
		
		public Task(String n){
			this(n, new Goal());
		}
		
		public Task(String n, Goal g){
			name = n;
			relatedGoal = g;
			completed = false;
		}
		//access methods
		public void setCompleted(boolean b){completed = b;}
		public boolean isCompleted(){return completed;}
		public void setGoal(Goal g){relatedGoal = g;}
		public Goal getGoal(){return relatedGoal;}
		public String getName(){return name;}
	}
	
//(inner class) Goal item
	public class Goal{
		private Color color;
		private String name;
		private Date dateAdded;
		private Date dateRemoved;
		private boolean active;
		
		//constructor for specified name, color
		public Goal(String n, Color c){
			name = n;
			color = c;
			active = true;
		}
		//default constructor
		public Goal(){this("", Color.white);}
		
		//access methods
		public void setColor(Color c){color = c;}
		public Color getColor(){return color;}
		public String getName(){return name;}
		public String getStartDate(){return dateFormat.format(dateAdded);}
		public String getEndDate(){return dateFormat.format(dateRemoved);}
	}
}
