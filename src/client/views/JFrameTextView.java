package client.views;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.JFrame;

import common.models.Grid;
import common.models.Player;

public class JFrameTextView implements View {
	
	public static final Dimension FRAME_SIZE = new Dimension(760, 760);
	public static final int GRID_TEXT_SIZE = 30;
	private TextArea textArea, console;
	
	public JFrameTextView(){
		JFrame frame = new JFrame("Bomberman");
		frame.setLayout(new BorderLayout());
		frame.setSize(FRAME_SIZE);
		
		// Add Text Area
		textArea = new TextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, GRID_TEXT_SIZE));
		frame.add(textArea, BorderLayout.CENTER);
		
		// Add Console
		console = new TextArea();
		console.setSize(FRAME_SIZE.width, FRAME_SIZE.height / 10);
		frame.add(console, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}

	@Override
	public void updateView(Grid grid) {
		textArea.setText(grid.toString());
		
	}
	
	private void console(String string){
		console.append(string);
	}

	@Override
	public void displayPlayerDead(Player player) {
		console(player.name + " has died.");
		
	}

	@Override
	public void displayConnectionAccepted() {
		console("Connection Accepted");
		
	}

	@Override
	public void displayConnectionRejected() {
		console("Connection Rejected");
		
	}

	@Override
	public void displayStartGame() {
		console("Game Started");
		
	}

	@Override
	public void displayEndGame(Grid grid, Player player) {
		console(player.name + "has ended the game");
	}	
}