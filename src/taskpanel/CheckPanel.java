package taskpanel;
//(component) colored checkBox, check toggles on-click

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class CheckPanel extends JPanel implements MouseListener{
	private boolean checkable;//whether checkbox responds to mouse clicks
	
	private boolean checked;
	private Color color;
	
	//constructor for specified color
	public CheckPanel(Color c){
		color = c;
		checked = false;//box unchecked on instantiation
		checkable = true;//box is checkable by default
		addMouseListener(this);
	}
	
	//constructor for default color (white)
	public CheckPanel(){this(Color.white);}
	
	public void setColor(Color c){
		color = c;
		repaint();
	}
	public void setChecked(boolean b){
		checked = b;
		repaint();
	}
	
	//conventional access methods
	public void setCheckable(boolean b){checkable = b;}
	public boolean isCheckable(){return checkable;}
	public boolean isChecked(){return checked;}
	
//(override) Paint a colored box in the center of checkpanel
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int width = getWidth();
		int height = getHeight();
		Graphics2D g2d = (Graphics2D)g;
		Rectangle box = new Rectangle((int)(width*.2), (int)(height*.2), (int)(width*.6), (int)(height*.6));
		g2d.setColor(color);
		g2d.fill(box);
		//draw light gray border around box
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(2));
		g2d.draw(box);
		if(checked){//draw a black check mark if box is checked
			g2d.setStroke(new BasicStroke(4));
			g2d.drawLine((int)(width*.12), (int)(height*.42), (int)(width*.42), (int)(height*.70));
			g2d.drawLine((int)(width*.94), (int)(height*.04), (int)(width*.42), (int)(height*.70));
		}
	}
	
//(implemented) MouseEvents (listener added in constructor)
	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		if(checkable)
			setChecked(!checked);
	}
	public void mouseReleased(MouseEvent e) {}
}