package integration;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.ArrayDeque;
import java.util.Queue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.GridGenerator;
import server.controllers.Server;
import client.controllers.Client;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Grid;

public class SystemTest {
	
	private static final int TEST_TIME = 1000;
	private TestServer testServer;
	private TestClient client;
	
	@Before
	public void setUp(){
		// Create and Start server
		testServer = new TestServer();
		assertTrue(!getServer().isAcceptingPlayers());
		
		testServer.start();
		assertTrue(getServer().isAcceptingPlayers());
		assertTrue(!getServer().isGameRunning());		
		
		// start and connect client to local server
		client = new TestClient();
		
		// Start game
		testServer.newGame(
			GridGenerator.createRandomGrid(new Dimension(4, 4), 2)
		);
		assertTrue(getServer().isGameRunning());
		assertTrue(!getServer().isAcceptingPlayers());
	}
	
	@After
	public void after(){
		testServer.stopGame();
		assertTrue(!getServer().isGameRunning());
	}

	@Test
	public void test() {
		try {
			Thread.sleep(TEST_TIME);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(client.receivedUpdate);
	}
	
	private Server getServer(){
		return testServer.server;
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
			server = new Server();
		}
		
		@Override
		public void start(){
			super.start();
			
			// Wait until server is ready before ending
			while(server == null || !server.isAcceptingPlayers());	
		}
		
		@Override
		public void run(){
			server.newGame(GridLoader.loadGrid("grid1.json"));
		}
		
		public void stopGame(){
			server.reset();
		}
		
		public void newGame(Grid grid){
			server.newGame(grid);
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
		
		private Queue<Event> events;
		private boolean receivedUpdate;
		private Grid grid;
		
		public TestClient(){
			events = new ArrayDeque<>();
			receivedUpdate = false;
		}
		
		public void pressKey(int keyCode){
			nwc.send(new GameKeyEvent(keyCode));
		}
		
		@Override
		public boolean isGameRunning() { return true; }
		
		@Override
		protected void processViewUpdate(ViewUpdateEvent event) {
			receivedUpdate = true;
			grid = event.getGrid();
		}
		
		@Override
		protected void processPlayerDead(PlayerDeadEvent event) {}
		@Override
		protected void processConnectionAccepted() {}
		@Override
		protected void processConnectionRejected() {}
		@Override
		protected void processWinEvent(WinEvent event) {}

		
	}

}
