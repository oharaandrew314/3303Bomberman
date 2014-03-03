package test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.event.KeyEvent;

import org.junit.After;
import org.junit.Test;

import test.integration.helpers.IntegrationHelper;
import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;
import common.models.Grid;
import common.models.Player;

public class SystemTest {
	
	private MockServer server;
	
	/** Stop game and check state */
	@After
	public void after(){
		server.reset();
		assertTrue(!server.isGameRunning());
	}

	@Test
	public void test() {
		// Create and Start server
		server = new MockServer();
		assertTrue(!server.isAcceptingPlayers());
		
		// Open new game
		server.newGame();
		assertTrue(server.isAcceptingPlayers());
		assertTrue(!server.isGameRunning());		
		
		// start and connect client to local server
		MockClient client = MockClient.startMockClient(server);
		
		// Ensure client knows game hasn't started yet
		assertTrue(!client.isGameRunning());
		
		// start game
		client.startGame();
		assertTrue(server.isGameRunning());
		
		
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
}
