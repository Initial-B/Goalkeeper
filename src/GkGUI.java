import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.*;
import javax.swing.*;





public class GkGUI extends JFrame{
	private JPanel GkPanel;
	
	public GkGUI(){
		this.guiConfig();
	}
	public void execute(){
		this.setVisible(true);
	}
	private void guiConfig(){
		Container contentPane = getContentPane();
		// enable explicit positioning of GUI components
		contentPane.setLayout(null);
        contentPane.setBackground( Color.white );
        setTitle("Goalkeeper");
        setSize(800,600);
        super.setLocation(200,160);
        
        
        
        //calendar component
      
        
        setVisible(true);
        
        
	}
}
