package integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.Server;
import client.controllers.Client;

import common.events.Event;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Grid;
import common.models.Player;

public class SystemTest {
	
	private TestServer testServer;
	private static Semaphore keySem, connectSem, viewUpdateSem;
	
	@Before
	public void setUp(){
		keySem = new Semaphore(1);
		connectSem = new Semaphore(1);
		viewUpdateSem = new Semaphore(1);
	}
	
	/** Stop game and check state */
	@After
	public void after(){
		testServer.stopGame();
		assertTrue(!getServer().isGameRunning());
	}

	@Test
	public void test() {	
		// Create and Start server
		testServer = new TestServer();
		assertTrue(!getServer().isAcceptingPlayers());
		
		testServer.start();
		assertTrue(getServer().isAcceptingPlayers());
		assertTrue(!getServer().isGameRunning());		
		
		// start and connect client to local server
		connectSem.acquireUninterruptibly();
		TestClient client = new TestClient();
		connectSem.acquireUninterruptibly();
		connectSem.release();
		
		// Wait for client to be accepted
		waitForKeyResponse();
		
		// start game
		client.pressKey(KeyEvent.VK_ENTER);
		
		waitForKeyResponse();
		assertTrue(getServer().isGameRunning());
		
		// Set player starting position
		Player p = IntegrationHelper.findPlayers(getGrid(), 1).get(0);
		getGrid().set(p, new Point(0, 0));
	
		// Move player right
		client.pressKey(KeyEvent.VK_D);
		waitForKeyResponse();
		assertEquals(new Point(1, 0), getGrid().find(p));
		
		// Wait for view update response
		viewUpdateSem.acquireUninterruptibly();
		viewUpdateSem.acquireUninterruptibly();
		viewUpdateSem.release();
		assertTrue(client.receivedUpdate);
	}
	
	private void waitForKeyResponse(){
		keySem.acquireUninterruptibly();
		keySem.release();
	}
	
	private Server getServer(){
		return testServer.server;
	}
	
	private Grid getGrid(){
		return getServer().getGrid();
	}
	
	// TODO try to remove from thread
	/**
	 * Test Server
	 * @author Andrew O'Hara
	 *
	 */
	private static class TestServer extends Thread {
		
		private Server server;
		
		public TestServer(){
			server = new MockServer();
		}
		
		@Override
		public void start(){
			super.start();
			
			// Wait until server is ready before ending
			while(server == null || !server.isAcceptingPlayers());	
		}
		
		@Override
		public void run(){
			server.newGame(GridLoader.loadGrid("test/testGrid2.json"));
		}
		
		public void stopGame(){
			server.reset();
		}
		
		private class MockServer extends Server {
			
			@Override
			public void receive(Event event){
				super.receive(event);
				if (event instanceof GameKeyEvent){
					keySem.release();
				}
			}
		}
	}
	
	// Test Client
	
	/**
	 * Test Client
	 * Logs events as they are received for testing
	 * @author Andrew O'Hara
	 *
	 */
	private static class TestClient extends Client {
		
		private boolean receivedUpdate;
		
		public TestClient(){
			receivedUpdate = false;
		}
		
		public void pressKey(int keyCode){
			keySem.acquireUninterruptibly();
			nwc.send(new GameKeyEvent(keyCode));
		}
		
		@Override
		public boolean isGameRunning() { return true; }
		
		@Override
		protected void processViewUpdate(ViewUpdateEvent event) {
			receivedUpdate = true;
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
		protected void processWinEvent(WinEvent event) {}

		
	}

}