package test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.event.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;
import common.models.Grid;
import common.models.Player;

public class SystemTest {
	
	private MockServer server;
	
	@Before
	public void setup(){
		server = new MockServer();
		assertTrue(server.isAcceptingConnections());
		assertTrue(!server.isGameRunning());
	}
	
	/** Stop game and check state */
	@After
	public void after(){
		if (server.isGameRunning()){
			server.endGame();
		}
		assertTrue(!server.isGameRunning());
		server.stop();
	}

	@Test
	public void test() {
		// Open new game
		server.newGame();
		assertTrue(server.isAcceptingConnections());
		assertTrue(!server.isGameRunning());
		
		// start and connect client to local server
		MockClient client = new MockClient(server);
		assertTrue(!client.isGameRunning());
		assertTrue(!server.isGameRunning());
		
		// start game
		client.startGame();
		assertTrue(server.isGameRunning());
		assertTrue(server.isGameRunning());
		
		// Set player starting position
		Player p = server.movePlayerTo(0, new Point(0, 0));
	
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
