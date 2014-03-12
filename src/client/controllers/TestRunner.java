package client.controllers;


import java.awt.event.KeyEvent;
import java.util.ArrayList;

import common.events.ConnectAcceptedEvent;
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
		//while(!connected){
			//waitForResponse(1);  //wait
			
		//}
		//waitCond(1000);
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
			//waitForResponse(100);
			//waitCond(1000);
		}
		if(timeOut){
			System.err.println("test client timed out waiting for Game key acknowledgment");
		}
		
		//waitForResponse(500);
		//waitCond(1000);
		stop();
		nwc.stopListening();
		
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		dead = true;
		nwc.stopListening();
		//notifyCond();
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
