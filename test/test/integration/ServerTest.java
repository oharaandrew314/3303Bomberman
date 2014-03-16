package test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.event.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.Server;
import test.integration.helpers.MockServer;
import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.models.units.Player;

public class ServerTest {
	
	private MockServer server;

	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		server.newGame(GridLoader.loadGrid("test/testGrid2.json"));
		assertFalse(server.isGameRunning());
	}
	
	@After
	public void tearDown(){
		server.stop();
	}

	@Test
	public void testOnePlayer() {
		// Connect one player
		send(1, new ConnectEvent(false));
		
		start(1);
		assertTrue(server.isGameRunning());
		
		Player p = server.movePlayerTo(1, new Point(0,0));

		goUp(p);  // Try going out of bounds
		checkPos(p, 0, 0);
		
		goDown(p);  // Try going to empty space
		checkPos(p, 0, 1);
		
		goRight(p);  // Try going into pillar
		checkPos(p, 0, 1);
		
		goDown(p);
		checkPos(p, 0, 2);
		
		goRight(p); // Try going into wall
		checkPos(p, 0, 2);
		
		goDown(p);
		checkPos(p, 0, 3);
		
		goRight(p);
		checkPos(p, 1, 3);
		
		goLeft(p);
		checkPos(p, 0, 3);
		
		goUp(p);
		checkPos(p, 0, 2);
		
		// Win game
		goDown(p);
		goRight(p);
		goRight(p);
		goUp(p);
		goRight(p);
		goUp(p);
		assertFalse(server.isGameRunning());
	}
	
	@Test
	public void testTwoPlayers(){
		// Connect two players
		send(1, new ConnectEvent(false));
		send(2, new ConnectEvent(false));
		
		start(1); // start the game
		
		Player p1 = server.movePlayerTo(1, new Point(0, 0));
		Player p2 = server.movePlayerTo(2, new Point(2, 1));
		assertPlayers(2);
		
		//Move p1
		goRight(p1);
		checkPos(p1, 1, 0);
		
		// Move p2
		goUp(p2);
		checkPos(p2, 2, 0);
		
		// Collide
		goLeft(p2);
		assertPlayers(0);
	}

	@Test
	public void testTooManyPlayers(){
		for (int i=0; i<Server.MAX_PLAYERS; i++){
			send(i, new ConnectEvent(false));
		}
		assertPlayers(Server.MAX_PLAYERS);
	}
	
	@Test
	public void testSpectators(){
		send(1, new ConnectEvent(false));
		send(2, new ConnectEvent(false));
		send(3, new ConnectEvent(true));
		start(1);
		assertPlayers(2);
	}
	
	@Test
	public void testPlayerCollision(){
		// Connect two players
		send(1, new ConnectEvent(false));
		send(2, new ConnectEvent(false));
		
		start(1); // Start the game
		
		// set initial positions besides each other
		Player p1 = server.movePlayerTo(1, new Point(0, 0));
		Player p2 = server.movePlayerTo(2, new Point(1, 0));
		
		// Try to move both players past each other; should ignore second command
		goRight(p1);
		goLeft(p2);
		
		assertPlayers(0); // Both players should be dead
	}
	
	// Helpers
	
	private void send(int playerId, Event event){
		event.setPlayerID(playerId);
		server.receive(event);
	}
	
	private void checkPos(Player player, int x, int y){
		assertEquals(new Point(x, y), server.getGrid().find(player));
	}
	
	private void assertPlayers(int expected){
		assertEquals(expected, server.getPlayers().size());
	}
	
	private void start(int playerId) { send(playerId, new GameKeyEvent(KeyEvent.VK_ENTER)); }
	private void goUp(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_UP)); }
	private void goLeft(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_LEFT)); }
	private void goDown(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_DOWN)); }
	private void goRight(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_RIGHT)); }

}
