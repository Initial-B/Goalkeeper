import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

//(inner class, component) rainbow color-choice panel (used in goal/task edit mode)
	public class ColorChoicePanel extends JPanel{
		JLabel textLabel;
		int width, height, index;
		boolean newGoalPicked;
		String goalName;
		Color color;
		
		public ColorChoicePanel(){
			super();
			setLayout(null);
			setOpaque(true);
			newGoalPicked = false;
			String goalName = GkGUI.noGoalString;
			color = Color.WHITE;
			index = -1;
			textLabel = new JLabel("?",SwingConstants.CENTER);//set label text + horizontal alignment
			textLabel.setVerticalAlignment(SwingConstants.CENTER);
			textLabel.setFont(textLabel.getFont().deriveFont((float)26));//set fontsize to 26
			textLabel.setForeground(Color.BLACK);
			add(textLabel);
		}
		public ColorChoicePanel(int n){//constructor with index
			this();
			index = n;
		}
		//access methods
		public Color getColor(){return color;}
		public void setColor(Color c){color = c;}
		public void setIndex(int num){index = num;}
		public int getIndex(){return index;}
		public void setGoalName(String name){goalName = name;}
		public String getGoalName(){return goalName;}
		
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			width = getWidth();
			height = getHeight();
			textLabel.setBounds(-width, -height, width*3, height*3);
			textLabel.setVisible(true);
			if(!newGoalPicked){
				g.setColor(Color.RED);
				g.fillPolygon(new int[]{0, width/2, width/2}, new int[]{0, 0, height/2}, 3);
				g.setColor(Color.ORANGE);
				g.fillPolygon(new int[]{width/2, width, width/2}, new int[]{0, 0, height/2}, 3);
				g.setColor(Color.YELLOW);
				g.fillPolygon(new int[]{width/2, width, width}, new int[]{height/2, 0, height/2}, 3);
				g.setColor(Color.GREEN);
				g.fillPolygon(new int[]{width/2, width, width}, new int[]{height/2, height/2, height}, 3);
				g.setColor(new Color(  0,128,128));//"teal"
				g.fillPolygon(new int[]{width/2, width, width/2}, new int[]{height/2, height, height}, 3);
				g.setColor(new Color(30,144,255));//"dodger blue"
				g.fillPolygon(new int[]{0, width/2, width/2}, new int[]{height, height/2, height}, 3);
				g.setColor(new Color(128,  0,128));//"purple"
				g.fillPolygon(new int[]{0, 0, width/2}, new int[]{height, height/2, height/2}, 3);
				g.setColor(new Color(255,105,180));//"hot pink"
				g.fillPolygon(new int[]{0, width/2, 0}, new int[]{0, height/2, height/2}, 3);
			}
			else
				setBackground(color);
		}
	}