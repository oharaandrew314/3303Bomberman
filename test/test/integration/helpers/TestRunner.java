package test.integration.helpers;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import client.controllers.Client;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.events.WinEvent;
import common.models.Grid;


public class TestRunner extends Client implements Runnable{
	private ArrayList<Integer> events;
	private Condition connected = new Condition(this), startCond = new Condition(this),
		keyCond = new Condition(this), gameOverCond = new Condition(this);
	private boolean dead = false;
	
	public TestRunner(ArrayList<Integer> events, int playerNumber){
		this.events = events;
	}

	@Override
	protected void processViewUpdate(Grid grid2) {}

	@Override
	protected synchronized void processConnectionAccepted() {
		connected.notifyCond();
	}

	@Override
	protected void processConnectionRejected() {}
	
	@Override
	public void run(){
		connected.waitCond();
		GameKeyEvent startEvent = new GameKeyEvent(KeyEvent.VK_ENTER);
		
		nwc.send(startEvent);
		startCond.waitCond();
		
		for(int i=0; !events.isEmpty() && i != events.size() && !dead&& isGameRunning(); i++) {
			nwc.send(new GameKeyEvent(events.get(i)));
			keyCond.waitCond();
		}
		
		// Having trouble synchronizing end of test.  Last resort
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		nwc.stopListening();
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		dead = true;
		nwc.stopListening();
	}
	
	@Override
	public void startGame(){
		super.startGame();
		startCond.notifyCond();
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	@Override
	protected synchronized void keyEventAcknowledged(){
		keyCond.notifyCond();
	}
	
	@Override
	public void endGame(WinEvent event){
		super.endGame(event);
		gameOverCond.notifyCond();
	}	
}
