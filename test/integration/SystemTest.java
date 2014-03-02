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
import common.models.Grid;
import common.models.Player;

public class SystemTest {
	
	private TestServer server;
	private static Semaphore keySem;
	
	@Before
	public void setUp(){
		keySem = new Semaphore(1);
	}
	
	/** Stop game and check state */
	@After
	public void after(){
		server.reset();
		assertTrue(!server.isGameRunning());
	}

	@Test
	public void test() {	
		// Create and Start server
		server = new TestServer();
		assertTrue(!server.isAcceptingPlayers());
		
		// Open new game
		server.newGame();
		assertTrue(server.isAcceptingPlayers());
		assertTrue(!server.isGameRunning());		
		
		// start and connect client to local server
		TestClient client = TestClient.startTestClient();
		
		// Ensure client knows game hasn't started yet
		assertTrue(!client.isGameRunning());
		
		// start game
		client.pressKey(KeyEvent.VK_ENTER);
		assertTrue(server.isGameRunning());
		assertTrue(client.isGameRunning());
		
		// Set player starting position
		Player p = IntegrationHelper.findPlayers(getGrid(), 1).get(0);
		getGrid().set(p, new Point(0, 0));
	
		// Move player right
		client.pressKey(KeyEvent.VK_D);
		assertEquals(new Point(1, 0), getGrid().find(p));
		
		// Wait for view update response
		client.waitForViewUpdate();
	}
	
	private Grid getGrid(){
		return server.getGrid();
	}
	
	private class TestServer extends Server {
		
		@Override
		public void receive(Event event){
			super.receive(event);
			if (event instanceof GameKeyEvent){
				keySem.release();
			}
		}
		
		public void newGame(){
			newGame(GridLoader.loadGrid("test/testGrid2.json"));
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
		
		private final Semaphore viewUpdateSem;
		private static Semaphore connectSem;
		
		private TestClient(){
			viewUpdateSem = new Semaphore(1);
		}
		
		// Factory
		
		public static TestClient startTestClient(){
			connectSem = new Semaphore(1);
			connectSem.acquireUninterruptibly();
			TestClient client = new TestClient();
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
		
		// Overrides
		
		@Override
		protected void processViewUpdate(ViewUpdateEvent event) {
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
	}

}
