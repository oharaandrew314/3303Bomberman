package client.views;

import java.awt.event.KeyListener;

import common.models.Grid;
import common.models.Player;


public class TextView implements View {
	
	@Override
	public void updateView(Grid grid){
		clearTerminal();
		
		System.out.println(grid); // TODO make nicer text view
	}
	
	private void clearTerminal(){
		try {
	        String os = System.getProperty("os.name");
	        
	        if (os.contains("Windows")) Runtime.getRuntime().exec("cls");
	        else Runtime.getRuntime().exec("clear");
	    }
	    catch (Exception e) {
	    }
		
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n");
	}

	@Override
	public void displayPlayerDead(Player player) {
		System.out.println(player.name + " has died.");
	}

	@Override
	public void displayConnectionAccepted() {
		System.out.println("Connection Accepted");
	}

	@Override
	public void displayConnectionRejected() {
		System.out.println("Connection Rejected");
	}

	@Override
	public void displayStartGame() {
		System.out.println("Game Started");
	}

	@Override
	public void displayEndGame(Grid grid, Player player) {
		System.out.println(grid);
		System.out.println(player.name + "has ended the game");
	}

	@Override
	public void close() {}

	@Override
	public void addKeyListener(KeyListener l) {}
}
