package client.controllers;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;


public class TestRunner extends Client implements Runnable{
	private ArrayList<Integer> events;
	private boolean connected = false;
	private boolean dead = false;
	
	public TestRunner(ArrayList<Integer> events){
		this.events = events;
	}

	@Override
	protected void processConnectionAccepted() {
		connected = true;
	}
	
	public void run(){
		while(!connected){
			waitForResponse(1);  //wait
			
		}
		int i = 0;
		while (!events.isEmpty() && i != events.size() && !dead&& isGameRunning()) {
			GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
			nwc.send(keyEvent);
			i++;
			waitForResponse(100);
		}
		waitForResponse(500);
		stop();
		nwc.stopListening();
		
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		dead = true;
		nwc.stopListening();
	}


	
	private void waitForResponse(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	public boolean isConnected(){
		return connected;
	}

	
	public void sendGameStartEvent(){
		GameKeyEvent startEvent = new GameKeyEvent(KeyEvent.VK_ENTER);
		nwc.send(startEvent);
	}
}
