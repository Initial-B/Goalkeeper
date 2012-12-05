package goalpanel;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import taskpanel.CheckPanel;

/*(component) 
 * 
 * 
*/
public class GoalPanel extends JPanel{
	private static JLabel titleLabel;
	final static int maxItems = 20;
	
	private int itemSpacing;
	private ArrayList<JLabel> goalLabels;
	private ArrayList<CheckPanel> goalBoxes;
	
	
	public GoalPanel(){
		setLayout(null);
		setBackground(Color.white);
		goalLabels = new ArrayList<JLabel>();
		goalBoxes = new ArrayList<CheckPanel>();
		
		titleLabel = new JLabel("My Goals");//default title of GoalBox
		titleLabel.setFont(new Font("Dialog", 0, 14));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setVisible(true);
		add(titleLabel);
	}
	
	
	public CheckPanel getGoalBox(int index){return goalBoxes.get(index);}
	public int getItemSpacing(){return itemSpacing;}
	
//(override) paint component + display items in goalLabels/goalBoxes according to size of GoalPanel
	public void paintComponent(Graphics g){
		//set item size based on max goals + 1 (for titleLabel)
		itemSpacing = (int)(this.getHeight()/(maxItems+1));
		titleLabel.setBounds(0,0, getWidth(), itemSpacing);
		for(int y = 0; y < goalLabels.size();y++){//for each item in goal list
			JLabel goalLabel = goalLabels.get(y);
			CheckPanel goalBox = goalBoxes.get(y);
			goalLabel.setBounds((int)(itemSpacing * 1.05), itemSpacing*(y+1),
					(int)(getWidth()-itemSpacing*1.1), (int)(itemSpacing*.95) );
			goalBox.setBounds(1, itemSpacing*(y+1), itemSpacing-1, itemSpacing-1);
		}
		super.paintComponent(g);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);//paint 1px black border around GoalPanel
	}

	//add named goal item + goal box of specified color to list of items
	public void addItem(String itemName, Color c){
		if(goalLabels.size() <= maxItems && goalBoxes.size() <= maxItems){
			JLabel goalLabel = new JLabel(itemName);
			goalLabel.setLayout(null);
			goalLabel.setVisible(true);
			goalLabels.add(goalLabel);
			add(goalLabel);
			
			CheckPanel goalBox = new CheckPanel(c);
			goalBox.setBackground(Color.white);
			goalBox.setCheckable(false);
			goalBox.setVisible(true);
			goalBoxes.add(goalBox);
			add(goalBox);
		}
		else
			System.out.println("goal list is full, goal \""+ itemName + "\" not added");
	}
	//remove item at specified index
	public void removeItem(int index){
		goalLabels.remove(index);
		goalBoxes.remove(index);
		repaint();
	}
	
//(override) set bounds + repaint()
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(x,y,w,h);
		repaint();
	}
	
}
