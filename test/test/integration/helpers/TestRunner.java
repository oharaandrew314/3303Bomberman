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
	private Condition connected = new Condition(), startCond = new Condition(),
		keyCond = new Condition(), gameOverCond = new Condition();
	private boolean dead = false;
	
	public TestRunner(ArrayList<Integer> events, int playerNumber){
		this.events = events;
	}

	@Override
	protected void processViewUpdate(Grid grid2) {}

	@Override
	protected synchronized void processConnectionAccepted() {
		notify(connected);
	}

	@Override
	protected void processConnectionRejected() {}
	
	@Override
	public void run(){
		wait(connected);
		GameKeyEvent startEvent = new GameKeyEvent(KeyEvent.VK_ENTER);
		
		nwc.send(startEvent);
		wait(startCond);
		
		for(int i=0; !events.isEmpty() && i != events.size() && !dead&& isGameRunning(); i++) {
			nwc.send(new GameKeyEvent(events.get(i)));
			wait(keyCond);
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
		notify(startCond);
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	@Override
	protected synchronized void keyEventAcknowledged(){
		notify(keyCond);
	}
	
	@Override
	public void endGame(WinEvent event){
		super.endGame(event);
		notify(gameOverCond);
	}
	
	// Synchronization Helpers
	
	/**
	 * Wait/Notify Synchronization helper.
	 * Essentially a mutable boolean that can be passed-by-reference
	 * @author Andrew O'Hara
	 */
	private class Condition {
		public boolean cond = false;
	}
	
	private synchronized void wait(Condition condition){
		while(!condition.cond){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private synchronized void notify(Condition condition){
		condition.cond = true;
		notify();
	}
}
