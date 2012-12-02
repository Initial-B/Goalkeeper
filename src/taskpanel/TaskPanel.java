package taskpanel;
/*(component) JPanel with white bg, regular JLabels separated by dotted lines,
	and a left side column for checkboxes
*/
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D.*;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TaskPanel extends JPanel{
	final static private int itemSpacing = 38; //default pixel spacing for items 
	final static private int itemFontSize = 12;//default itemLabel font size
	private static Color bgColor = Color.white; 
	private ArrayList<JLabel> itemList;
	private ArrayList<CheckPanel> checkPanels;
	private ArrayList<ActionListener> listeners = new ArrayList<ActionListener>();
		
	public TaskPanel(){
		itemList = new ArrayList<JLabel>();
		setBackground(bgColor);
		setLayout(null);
		checkPanels = new ArrayList<CheckPanel>();
	}
	
	//set text of JLabel at specified index to the given string
	public void setText(int index, String s){
		itemList.get(index).setText(s);
		itemList.get(index).repaint();
	}
	
	//set color of checkbox at specified index (CheckPanel handles repaint)
	public void setColor(int index, Color c){checkPanels.get(index).setColor(c);}
	
	//set checkbox at specified index to checked/unchecked (CheckPanel handles repaint & calls task strikethrough)
	public void setChecked(int index, boolean checked){checkPanels.get(index).setChecked(checked);}
		
	//set strikethrough attribute of JLabel text at specificed index
	public void setStrikeThrough(int index, boolean striked){
		Map attributes = itemList.get(index).getFont().getAttributes();
		if(striked)//strikethrough tasklabel text if box is checked
			attributes.put(TextAttribute.STRIKETHROUGH, TextAttribute.STRIKETHROUGH_ON);
		else//turn off strikethrough if box is not checked
			attributes.put(TextAttribute.STRIKETHROUGH, false);
		itemList.get(index).setFont(new Font(attributes));
	}
	
	//set visibility of an item at specified index and its corresponding checkbox
	public void setItemVisibility(int index, boolean visible){
		itemList.get(index).setVisible(visible);
		checkPanels.get(index).setVisible(visible);
	}
	
	public int getListSize(){return itemList.size();}
	
	//(override) paint inner area of numPanel based on value of isSelected
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		g.drawRect(0, 0, getWidth()-1, getHeight()-1);
		Stroke dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{4}, 0);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setStroke(dashedStroke);
		//place regular horizontal lines spaced using itemSpacing
		for(double y = 0; y < getHeight();y+=itemSpacing){
			Line2D line = new Line2D.Double(0, y, getWidth(), y);
			g2d.draw(line);
		}
		dashedStroke = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0);
		g2d.setStroke(dashedStroke);
		Line2D line = new Line2D.Double(itemSpacing, 0, itemSpacing, getHeight());
		g2d.draw(line);
	}
	
	//draw items in itemList based on component size
	public void fillItems(){
		itemList.clear();
		checkPanels.clear();
		for(double y = 0; y < getHeight();y+=itemSpacing){
			JLabel itemLabel =  new JLabel("");
			itemLabel.setBounds((int)(1.2*itemSpacing), (int)(.2*itemSpacing + y),
				(int)(getWidth() - 1.3*itemSpacing), (int)(.75*itemSpacing));
			itemLabel.setFont(new Font("Dialog", Font.PLAIN, itemFontSize));
			itemLabel.setVisible(true);
			itemList.add(itemLabel);
			add(itemLabel);
			
			TaskCheckbox cBox = new TaskCheckbox();
			cBox.setBounds((int)(.1*itemSpacing), (int)(.1*itemSpacing + y), 
				(int)(.8*itemSpacing), (int)(.8*itemSpacing));
			cBox.setBackground(Color.white);
			cBox.setVisible(true);
			checkPanels.add(cBox);
			add(cBox);
		}
		repaint();
	}	
	
	//(override) set bounds + fill itemLabels (using new bounds)
	public void setBounds(int x, int y, int w, int h){
		super.setBounds(x,y,w,h);
		fillItems();
	}
//EventListener stuff **************************************************************************
	public void addActionListener(ActionListener listener){listeners.add(listener);}
	public void removeActionListener(ActionListener listener){listeners.remove(listener);}
	//send ActionEvent with "<index> <checked>" as the command String
	private void makeEvent(int index, boolean checked){
		String commandString = String.valueOf(index) + " " + String.valueOf(checked);
		ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, commandString);
		for(ActionListener listener : listeners)
			listener.actionPerformed(event);
	}
	
//end of EventListener stuff **********************************************************************

	
//(inner class) extended checkPanel, interacts with task items
	private class TaskCheckbox extends CheckPanel{
		//constructor for specified color
		public TaskCheckbox(Color c){super(c);}
	
		//constructor for default color (white)
		public TaskCheckbox(){super();}

	//(override) set value of checked + strikethrough of corresponding task
		public void setChecked(boolean checked){
			super.setChecked(checked);
			setStrikeThrough(checkPanels.indexOf(this), checked);
		}

	//(override) checkbox mouseListener interaction with task item
		public void mousePressed(MouseEvent e) {
			super.mousePressed(e);
			//send actionevent indicating checkbox index and whether it is checked
			makeEvent(checkPanels.indexOf(this), isChecked());
		}
	}
}





