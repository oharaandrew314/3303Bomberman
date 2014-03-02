package client.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.TextArea;

import javax.swing.JFrame;

import common.models.Grid;
import common.models.Player;

public class JFrameTextView implements View {
	
	private TextArea textArea, console;
	
	public JFrameTextView(){
		JFrame frame = new JFrame("Bomberman");
		frame.setLayout(new BorderLayout());
		frame.setSize(760, 760);
		
		// Add Text Area
		textArea = new TextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, 30));
		frame.add(textArea, BorderLayout.CENTER);
		
		// Add Console
		console = new TextArea();
		console.setSize(760, 80);
		frame.add(console, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}

	@Override
	public void updateView(Grid grid) {
		textArea.setText(grid.toString());
		
	}
	
	private void console(String string){
		console.setText(console.getText() + string);
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