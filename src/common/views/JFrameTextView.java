package common.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import common.controllers.GameController;
import common.models.Grid;

public abstract class JFrameTextView extends AbstractView {
	
	public static final Dimension FRAME_SIZE = new Dimension(760, 760);
	public static final int GRID_TEXT_SIZE = 30;
	public static final String LINE_SEP = System.getProperty("line.separator");
	private TextArea textArea, console;
	protected final JFrame frame;
	
	public JFrameTextView(GameController gc){	
		gc.setView(this);
		
		frame = new JFrame("Bomberman");
		frame.setLayout(new BorderLayout());
		frame.setSize(FRAME_SIZE);
		
		// Add Text Area
		textArea = new TextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, GRID_TEXT_SIZE));
		textArea.setEditable(false);
		frame.add(textArea, BorderLayout.CENTER);
		
		// Add Console
		console = new TextArea();
		console.setSize(FRAME_SIZE.width, FRAME_SIZE.height / 10);
		console.setEditable(false);
		frame.add(console, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	public void addJMenuBar(JMenuBar menuBar){
		frame.setJMenuBar(menuBar);
	}
	
	protected void setTitle(String title){
		frame.setTitle(title);
	}

	@Override
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}
	
	@Override
	public String updateView(Grid grid) {
		textArea.setText(grid.toString());
		return null;
	}

	@Override
	public void addKeyListener(KeyListener l) {
		frame.addKeyListener(l);
		textArea.addKeyListener(l);
		console.addKeyListener(l);
	}
	
	public void displayMessage(String message){
		console.append(message + LINE_SEP);
	}
}