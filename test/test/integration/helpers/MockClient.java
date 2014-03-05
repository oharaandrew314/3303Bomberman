package test.integration.helpers;

import java.awt.event.KeyEvent;

import test.helpers.Condition;
import client.controllers.Client;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.models.Grid;

/**
 * Test Client
 * Logs events as they are received for testing
 * @author Andrew O'Hara
 *
 */
public class MockClient extends Client {
	
	private final Condition connectCond, updateCond, keyCond;
	private boolean accepted;
	
	public MockClient(){
		keyCond = new Condition();
		updateCond = new Condition();
		connectCond = new Condition();

		connectCond.waitCond();
	}
	
	// Helpers
	
	public void pressKey(int keyCode){
		nwc.send(new GameKeyEvent(keyCode));
		keyCond.waitCond();
	}
	
	public void waitForViewUpdate(){
		updateCond.waitCond();
	}
	
	public void startGame(){
		pressKey(KeyEvent.VK_ENTER);
	}
	
	public boolean wasAccepted(){
		return accepted;
	}
	
	// Overrides
	
	@Override
	protected void processViewUpdate(Grid grid) {
		updateCond.notifyCond();
	}
	
	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {}
	@Override
	protected void processConnectionAccepted() {
		while (connectCond == null);
		accepted = true;
		connectCond.notifyCond();
	}
	@Override
	protected void processConnectionRejected() {
		while (connectCond == null);
		accepted = false;
		connectCond.notifyCond();
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	@Override
	protected void keyEventAcknowledged(){
		keyCond.notifyCond();
	}
}