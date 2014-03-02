package client.controllers;

import java.util.ArrayList;

import common.controllers.NetworkController;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;


public class TestRunner extends Client implements Runnable{
	
	private ArrayList<Integer> events;
	private int playerNumber;
	private boolean connected = false;
	
	public TestRunner(ArrayList<Integer> events, int playerNumber){

		super(NetworkController.DEFAULT_CLIENT_PORT + 5 + playerNumber);
		System.out.println("playerNumber: " + playerNumber);
		System.out.println("Port: " + (NetworkController.DEFAULT_CLIENT_PORT + 5 + playerNumber));
		this.events = events;
		this.playerNumber = playerNumber;
		
	}

	@Override
	protected void processViewUpdate(ViewUpdateEvent event) {
		this.grid = event.getGrid();
		
	}

	@Override
	protected void processConnectionAccepted() {
		// TODO Auto-generated method stub
		System.out.println("Player: " + playerNumber + " connected to the server");
		connected = true;
		
	}

	@Override
	protected void processConnectionRejected() {
		// TODO Auto-generated method stub
		System.out.println("Player: " + playerNumber + " could not connect to server");
		
	}
	
	public void run(){
		
		while(!connected){
			//wait
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			for(int i = 0; i != events.size(); i++){
				GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
				keyEvent.setPlayerID(i + 1);
				nwc.send(keyEvent);
				
			}
			
			
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				nwc.stopListening();
			
		
		
			
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		// TODO Auto-generated method stub
		System.out.println("PLAYER: " + playerNumber + ", YOU ARE DEAD");
		
	}

	@Override
	protected void processWinEvent(WinEvent event) {
		// TODO Auto-generated method stub
		System.out.println("PLAYER: " + playerNumber + ", YOU WIN");
		
	}

	@Override
	public boolean isGameRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}
