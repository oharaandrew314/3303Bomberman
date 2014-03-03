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
		
		nwc.send( new GameKeyEvent(KeyEvent.VK_ENTER));
		startCond.waitCond();
		
		for(int i=0; !events.isEmpty() && i != events.size() && isGameRunning(); i++) {
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
