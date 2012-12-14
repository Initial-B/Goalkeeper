import java.awt.*;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.*;
public class Planner implements Serializable{
	
	final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//default dateFormat for calendar dates
	
	private String name;
	private TreeMap<Integer, Day> schedule;
	private ArrayList<Goal> goals;
	private ArrayList<Goal> pastGoals;
	private Calendar calendar;//calendar for setting Goal start/end dates
	
	//default constructor
	public Planner(){
		schedule = new TreeMap<Integer, Day>();
		goals = new ArrayList<Goal>();
		pastGoals = new ArrayList<Goal>();
	}
	//constructor with specified name
	public Planner(String n){
		this();
		name = n;
	}
	
	//conventional access methods
	public String getName(){return name;}
	public Goal getGoal(int index){return goals.get(index);}
	public ArrayList<Goal> getGoals(){return goals;}
	
	
	//return Day with associated date, create one if DNE
	public Day getDay(int ymd){
		if(schedule.containsKey(ymd))
			return schedule.get(ymd);
		else{
			Day retDay = new Day(ymd);
			schedule.put(ymd,retDay);
			return retDay;
		}
	}
	

	
	public ArrayList<String> getGoalNames(){
		ArrayList<String> goalNames = new ArrayList<String>();
		for(Goal g : goals)
			goalNames.add(g.getName());
		return goalNames;
	}
	public ArrayList<Color> getGoalColors(){
		ArrayList<Color> goalColors = new ArrayList<Color>();
		for(Goal g : goals)
			goalColors.add(g.getColor());
		return goalColors;		
	}
	
	
	public ArrayList<Task> getTasks(int ymd){return getDay(ymd).tasks;}//returns an arraylist of tasks of the specified date
	public Task getTask(int ymd, int index){return getDay(ymd).tasks.get(index);}//return task at specified index of specified date
	public void removeTask(int ymd, int index){getDay(ymd).tasks.remove(index);}
	
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
	
	//remove a goal (todo: remove goal/color from all related tasks)
	public void removeGoal(Goal g){
		calendar = Calendar.getInstance();
		g.dateAdded = calendar.getTime();
		g.active = false;
		pastGoals.add(g);
		goals.remove(g);
	}
	//remove goal at specified index
	public void removeGoal(int index){removeGoal(goals.get(index));}
	


	
	
//(inner class) Day item
	public class Day implements Serializable{
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
	public class Task implements Serializable{
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
		public void removeGoal(){relatedGoal = new Goal();}
		public Goal getGoal(){return relatedGoal;}
		public void setName(String n){name = n;}
		public String getName(){return name;}
	}
	
//(inner class) Goal item
	public class Goal implements Serializable{
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
		public void setName(String n){name = n;}
		public String getName(){return name;}
		public String getStartDate(){return dateFormat.format(dateAdded);}
		public String getEndDate(){return dateFormat.format(dateRemoved);}
		
		@Override //two goals are equal if their names are equal
		public boolean equals(Object other){
			if (other == null) return false;
		    if (other == this) return true;
		    if (!(other instanceof Goal))return false;
			Goal otherGoal = (Goal)other;
			return name.equals(otherGoal.getName());
		}
	}
}
