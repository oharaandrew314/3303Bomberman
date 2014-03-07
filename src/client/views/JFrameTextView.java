package client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import common.models.Grid;
import common.models.Player;

public class JFrameTextView implements View {
	
	public static final Dimension FRAME_SIZE = new Dimension(760, 760);
	public static final int GRID_TEXT_SIZE = 30;
	private TextArea textArea, console;
	private final JFrame frame;
	
	public JFrameTextView(){
		this(null);
	}
	
	public JFrameTextView(JMenuBar menuBar){
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
		
		// Add Menu Bar
		if (menuBar != null){
			frame.setJMenuBar(menuBar);
		}
		
		frame.setVisible(true);
	}

	@Override
	public void updateView(Grid grid) {
		textArea.setText(grid.toString());	
	}

	@Override
	public void displayPlayerDead(Player player) {
		console.append(player.name + " has died.");
	}

	@Override
	public void displayConnectionAccepted() {
		console.append("Connection Accepted");
		
	}

	@Override
	public void displayConnectionRejected() {
		console.append("Connection Rejected");
		
	}

	@Override
	public void displayStartGame() {
		console.append("Game Started");
		
	}

	@Override
	public void displayEndGame(Grid grid, Player player) {
		console.append(player.name + "has ended the game");
	}

	@Override
	public void close() {
		frame.setVisible(false);
		frame.dispose();
	}

	@Override
	public void addKeyListener(KeyListener l) {
		frame.addKeyListener(l);
		textArea.addKeyListener(l);
		console.addKeyListener(l);
	}
}