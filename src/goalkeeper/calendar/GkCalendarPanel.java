/*negligable bug: sometimes clicking on a combo box triggers two action events
		to reproduce, click a faded NumPanel (to change month), then click textFields of yearBox, monthBox, yearBox (in that order)

*/
package goalkeeper.calendar;


import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.text.*;
import java.util.*;

public class GkCalendarPanel extends JPanel{
	final static String[] months = {"January","February","March","April","May","June",
		"July","August","September","October","November","December"};
	final static String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
	final static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");//default dateFormat for calendar dates
	
	final static int defaultWidth = 210;
	final static int defaultHeight = 160;
	final static int u = (defaultHeight / 16);//length unit to maintain size proportions of subcomponents
	final static Color defaultColorScheme = (new Color(50,150,255));
	
	private static NumPanel lastSelectedNumPanel; // most recently selected NumPanel
	
	private int width, height;
	private JComboBox yearBox, monthBox;
	private String[] years;
	private NumPanel[] dayPanels;
	private NumPanel[][] numPanels;
	private Color colorScheme;
	private YearBoxHandler yearHandler;
	private MonthBoxHandler monthHandler;
	
	private Calendar calendar;
	private int year, month, date;//most recently selected calendar field values
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();

	
	public GkCalendarPanel(){
		this.setSize(defaultWidth, defaultHeight);
		this.setLayout(null);
		width = this.getWidth();
		height = this.getHeight();
		colorScheme = defaultColorScheme;
		this.setBackground(colorScheme);
		
		//initialize year, month, date to current time
		calendar = Calendar.getInstance();
		calendar.getTime();
		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH) + 1;//Java Calendar months start at 0
		date = calendar.get(Calendar.DAY_OF_MONTH);
		
		//configure yearBox using current decade
		years = new String[10];
		int decadeIter = (year - (year % 10));
		for(int x = 0;x < 10;x++){
			years[x] = String.valueOf(decadeIter);
			decadeIter++;
		}
		yearBox = new JComboBox(years);
		yearBox.setSelectedIndex(year % 10);//initial year selected = current year
		yearBox.setEditable(true);
		yearBox.setBounds(0, 0, 9*u, 2*u);
		yearHandler = new YearBoxHandler();
		yearBox.addActionListener(yearHandler);
		this.add(yearBox);
			
		//configure monthBox
		monthBox = new JComboBox(months);
		monthBox.setSelectedIndex(month - 1);//initial month selected = current month
		monthBox.setEditable(true);
		monthBox.setBounds(9*u, 0, 12*u, 2*u);
		monthHandler = new MonthBoxHandler();
		monthBox.addActionListener(monthHandler);
		this.add(monthBox);
		
		//configure dayPanels
		dayPanels = new NumPanel[7];
		for(int x = 0;x < 7;x++){
			dayPanels[x] = new NumPanel(x, colorScheme, false);
			dayPanels[x].setSize(3*u, 2*u);
			dayPanels[x].setLocation(3*u*x, 2*u);
			dayPanels[x].numLabel.setText(days[x]);
			dayPanels[x].setVisible(true);
			this.add(dayPanels[x]);
		}
		
		//configure numPanels
		numPanels = new NumPanel[6][7];
		for(int y = 0;y < 6;y++){
			for(int x = 0;x < 7; x++){
				numPanels[y][x] = new NumPanel((y*7 + x), colorScheme, true);
				numPanels[y][x].setSize(3*u, 2*u);
				numPanels[y][x].setLocation(3*u*x, 2*u*y+4*u);
				numPanels[y][x].setVisible(true);
				this.add(numPanels[y][x]);
			}
		}
		updateCalendar();
	}
	
	//redraw all NumPanels based on selected year/month
	private void updateCalendar(){
		//change comboBox text to match selected year/month
		yearBox.getEditor().setItem(String.valueOf(year));
		monthBox.getEditor().setItem(months[month - 1]);
		//set calendar to first day of selected month
		calendar.set(year, month-1, 1);
		int weekday = calendar.get(Calendar.DAY_OF_WEEK);
		calendar.add(Calendar.DATE, -1*(weekday - 1));//set calendar to first day of selected week
		//iterate through all NumPanels
		for(int y = 0;y < 6;y++){
			for(int x = 0;x < 7; x++){
				//store formatted date string in NumPanel id
				numPanels[y][x].setValues(calendar.get(Calendar.DATE), dateFormat.format(calendar.getTime()));
				//deselect NumPanel
				numPanels[y][x].isSelected = false;
				if((calendar.get(Calendar.MONTH) + 1) == month){
					numPanels[y][x].faded = false;
					if(date==numPanels[y][x].value && year==calendar.get(Calendar.YEAR)){
						numPanels[y][x].isSelected = true;
						lastSelectedNumPanel = numPanels[y][x];
					}
				}
				else
					numPanels[y][x].faded = true;
				numPanels[y][x].repaint();
				calendar.add(Calendar.DATE, 1);
			}	
		}
	}
	
	//yearBox action handler
	private class YearBoxHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			try{year = Integer.parseInt((String)((JComboBox)event.getSource()).getSelectedItem());}
				catch(NumberFormatException e){}
			updateCalendar();
			makeEvent();
		}
	}
		
	//monthBox action handler
	private class MonthBoxHandler implements ActionListener{
		public void actionPerformed(ActionEvent event){
			String monthIn = ((String)((JComboBox)event.getSource()).getSelectedItem());
			for(int x=0;x<months.length;x++){
				if(monthIn.equalsIgnoreCase(months[x])){
					month = x+1;
					updateCalendar();
					makeEvent();
					break;
				}
			}
		}
	}
	
	public void addActionListener(ActionListener listener){listeners.add(listener);}
	public void removeActionListener(ActionListener listener){listeners.remove(listener);}
	//send an actionEvent with "yyyyMMdd" as the command string
	private void makeEvent(){
		String digitString = String.valueOf(year*10000 + month*100 + date);
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, digitString);
		for(ActionListener listener : listeners)
			listener.actionPerformed(event);
	}
	
	//send an actionEvent with "yyyy-MM-dd" as the command string
	/*
	private void makeEvent(){
		String formattedCommand = String.valueOf(year) + "-" + String.format("%02d", month) + "-" + String.valueOf(date);
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, formattedCommand);
		for(ActionListener listener : listeners)
			listener.actionPerformed(event);
	}
	*/
	
//(Inner Class) subclass of JPanel for date items on GkCalendar	**************************************************
	private class NumPanel extends JPanel implements MouseListener{
		private JLabel numLabel;
		private String id;
		private int value;
		private Color colorScheme;
		private boolean selectable, isSelected, faded, darkScheme;
		
		//primary constructor
		public NumPanel(int val, Color color, boolean select){
			value = val;
			this.colorScheme = color;
			darkScheme = false;
			//if colorScheme is "dark" (by weighted RGB brightness)
			if((.2126*colorScheme.getRed() + .7152*colorScheme.getGreen() + .0722*colorScheme.getBlue()) < 128)
				darkScheme = true;
			selectable = select; //NumPanel is selectable by default
			isSelected = false;
			setBackground(colorScheme);
			numLabel = new JLabel(String.valueOf(value));
			numLabel.setHorizontalAlignment(SwingConstants.CENTER);
			numLabel.setVerticalAlignment(SwingConstants.TOP);
			numLabel.setBorder(new EmptyBorder(new Insets(-3,0,0,0))); //trim top text inset
			if(selectable){//add mouselistener to both numPanel & numLabel (
				numLabel.addMouseListener(this);
				addMouseListener(this);
			}
			this.add(numLabel);
		}
		
		//(override) sets size of both NumPanel and its NumLabel
		public void setSize(int w, int h){
			super.setSize(w, h);
			numLabel.setPreferredSize(new Dimension(w, h));
			numLabel.setLocation(0, 0);
		}
		
		//(override) sets Font of both NumLabel and NumPanel
		public void setFont(Font font){
			super.setFont(font);
			try{numLabel.setFont(font);}
				catch (NullPointerException e){}
		}
		
		//(override) two NumPanels are equal if their value and id are the same
		public boolean equals(NumPanel rhs){return (value==rhs.value && id.equals(rhs.id));}
		
		//(override) paint inner area of numPanel based on value of isSelected
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if(!isSelected)//if numpanel is unselected, set fill color to white
				g.setColor(Color.white);
			else//if numpanel is selected, set fill to colorScheme
				g.setColor(colorScheme);
			if(!faded){
				if(darkScheme)
					numLabel.setForeground(Color.white);
				else 
					numLabel.setForeground(Color.black);
			}
			else
				numLabel.setForeground(Color.lightGray);
			g.fillRect(1,1,this.getWidth()-2,this.getHeight()-2);//fill center of NumPanel
		}
		
		//assign String + int values
		public void setValues(int v, String s){
			value = v;
			id = s;
			numLabel.setText(String.valueOf(v));
		}
		
		//select this numpanel & deselect previously-selected numpanel
		public void toggleSelection(){
			if(lastSelectedNumPanel==null || lastSelectedNumPanel.equals(this))
				isSelected = !isSelected;
			else{
				lastSelectedNumPanel.isSelected = false;
				lastSelectedNumPanel.repaint();
				isSelected = true;
			}
			date = value;
			lastSelectedNumPanel = this;
			//read month from this.id
			int newMonth = Integer.parseInt(id.substring(5,7));
			//if new month has been selected, update calendar
			if(month != newMonth){
				month = newMonth;
				year = Integer.parseInt(id.substring(0,4));
				makeEvent();
				updateCalendar();
			}
			else if(isSelected && !faded)
				makeEvent();
			repaint();
		}
		//(implemented) MouseEvents for NumPanel (listener added to NumLabel in constructor)
		public void mouseClicked(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {toggleSelection();}
		public void mouseReleased(MouseEvent e) {}
	}
}
