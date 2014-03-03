package integration.helpers;

import java.awt.event.KeyEvent;
import java.util.concurrent.Semaphore;

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
	
	private final Semaphore viewUpdateSem;
	private static Semaphore connectSem, keySem;
	
	private MockClient(MockServer mockServer){
		viewUpdateSem = new Semaphore(1);
		keySem = mockServer.keySem;
	}
	
	// Factory
	
	public static MockClient startMockClient(MockServer mockServer){
		connectSem = new Semaphore(1);
		connectSem.acquireUninterruptibly();
		MockClient client = new MockClient(mockServer);
		connectSem.acquireUninterruptibly();
		connectSem.release();
		return client;
	}
	
	// Helpers
	
	public void pressKey(int keyCode){
		keySem.acquireUninterruptibly();
		nwc.send(new GameKeyEvent(keyCode));
		keySem.acquireUninterruptibly();
		keySem.release();
	}
	
	public void waitForViewUpdate(){
		viewUpdateSem.acquireUninterruptibly();
		viewUpdateSem.acquireUninterruptibly();
		viewUpdateSem.release();
	}
	
	public void startGame(){
		pressKey(KeyEvent.VK_ENTER);
	}
	
	// Overrides
	
	@Override
	protected void processViewUpdate(Grid grid) {
		viewUpdateSem.release();
	}
	
	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {}
	@Override
	protected void processConnectionAccepted() {
		connectSem.release();
	}
	@Override
	protected void processConnectionRejected() {
		connectSem.release();
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
}