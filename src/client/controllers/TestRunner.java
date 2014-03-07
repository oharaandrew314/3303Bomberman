package client.controllers;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.models.Grid;


public class TestRunner extends Client implements Runnable{
	private ArrayList<Integer> events;
	private boolean connected = false;
	private boolean dead = false;
	//public final Point startLoc;
	
	public TestRunner(ArrayList<Integer> events){
		this(events, null);
	}
	
	public TestRunner(ArrayList<Integer> events, Point startLoc){
		super("127.0.0.1", startLoc);
		this.events = events;
		//this.startLoc = startLoc;
	}

	@Override
	protected void processViewUpdate(Grid grid2) {}

	@Override
	protected void processConnectionAccepted() {
		connected = true;
	}

	@Override
	protected void processConnectionRejected() {}
	
	public void run(){
		while(!connected){
			waitForResponse(1);  //wait
			
		}
		GameKeyEvent startEvent = new GameKeyEvent(KeyEvent.VK_ENTER);
		nwc.send(startEvent);
		waitForResponse(100);
		int i = 0;
		while (!events.isEmpty() && i != events.size() && !dead&& isGameRunning()) {
			GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
			nwc.send(keyEvent);
			i++;
			waitForResponse(100);
		}
		waitForResponse(500);
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
}
