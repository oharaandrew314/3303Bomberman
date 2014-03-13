package client.controllers;


import java.awt.event.KeyEvent;
import java.util.ArrayList;

import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
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
		super.processConnectionAccepted();
		connected = true;
	}

	
	public void run(){
		int i = 0;
		boolean timeOut = false;
			
		while (!events.isEmpty() && i != events.size() && !dead&& isGameRunning() && !timeOut) {
			GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
			nwc.send(keyEvent);
			long lastKeyPress = System.currentTimeMillis();
			long timeBeforeNextPress = 200;
			timeOut = false;
			waitFor(GameKeyEventAck.class);
			i++;
			long currentTime = System.currentTimeMillis();
			while(currentTime - lastKeyPress < timeBeforeNextPress){
				currentTime = System.currentTimeMillis();
			}
		}
		if(timeOut){
			System.err.println("test client timed out waiting for Game key acknowledgment");
		}
		stop();
		nwc.stopListening();
		
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		dead = true;
		nwc.stopListening();
	}


	
	private void waitForResponse(int millis){
		long startTime = System.currentTimeMillis();
		while(System.currentTimeMillis() - startTime < millis){};
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
	
	public synchronized void waitFor(Class<? extends Event> eventType){
		try {
			wait(1000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
	}
	
	@Override
	public synchronized Event receive(Event event) {
		if(event instanceof GameKeyEventAck){
			notify();
		}
		return super.receive(event);
	}
}
