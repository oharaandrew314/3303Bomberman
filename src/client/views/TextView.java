package client.views;

import java.util.Queue;
import java.util.ArrayDeque;

import common.models.Grid;
import common.models.Player;


public class TextView implements View {
	
	Queue<String> messageQueue;
	
	public TextView(){
		messageQueue = new ArrayDeque<String>();
	}
	
	@Override
	public void updateView(Grid grid){
		clearTerminal();
		
		System.out.println(grid); // TODO make nicer text view
		while (!messageQueue.isEmpty()){
			System.out.println(messageQueue.remove());
		}
	}
	
	private void clearTerminal(){
		try {
	        String os = System.getProperty("os.name");
	        
	        if (os.contains("Windows")) Runtime.getRuntime().exec("cls");
	        else Runtime.getRuntime().exec("clear");
	    }
	    catch (Exception e) {
	    }
		
		System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n"); // no 100% reliable way to clear a terminal 
	}

	@Override
	public void displayPlayerDead(Player player) {
		messageQueue.add(player.name + " has died.");
	}

	@Override
	public void displayConnectionAccepted() {
		System.out.println("Connection Accepted"); //skip queue, connection status needs to be known immediately
	}

	@Override
	public void displayConnectionRejected() {
		System.out.println("Connection Rejected");
	}

	@Override
	public void displayWin(Player player) {
		messageQueue.add(player.name + " has won.");
	}
}
