package common.views;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ConsoleView extends JTextArea {
	
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	public ConsoleView(){
		setEditable(false);
		setBorder(BorderFactory.createLineBorder(Color.GRAY));
	}
	
	@Override
	public void append(String msg){
		super.append(String.format("%s%s", msg, LINE_SEP));
	}
	
	@Override
	public Dimension getPreferredSize(){
		return new Dimension(400, 200);
	}
}
