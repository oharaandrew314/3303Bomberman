package test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.Server;
import test.integration.helpers.IntegrationHelper;
import test.integration.helpers.MockServer;
import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.models.Player;

public class ServerTest {
	
	private MockServer server;

	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		server.newGame(GridLoader.loadGrid("grid1.json"));
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
		
		// Ensure Player starts at (0, 0)
		List<Player> players = IntegrationHelper.findPlayers(server.getGrid(), 1);
		Player p = players.get(0);
		server.getGrid().remove(p);
		server.getGrid().set(p, new Point(0, 0));
		
		start(p);
		assertTrue(server.isGameRunning());
		
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
		// Connect one player
		send(1, new ConnectEvent(false));
		send(2, new ConnectEvent(false));
		
		List<Player> players = IntegrationHelper.findPlayers(server.getGrid(), 2);
		Player p1 = players.get(0);
		server.getGrid().remove(p1);
		server.getGrid().set(p1, new Point(0, 0));
		
		Player p2 = players.get(1);
		server.getGrid().remove(p2);
		server.getGrid().set(p2, new Point(2, 1));
		
		start(p1);
		
		//Move p1
		goRight(p1);
		checkPos(p1, 1, 0);
		
		// Move p2
		goUp(p2);
		checkPos(p2, 2, 0);
		
		// Collide
		goLeft(p2);
		IntegrationHelper.findPlayers(server.getGrid(), 0);
	}
	
	/**
	 * This test creates one too many players, and then ensures that only
	 * the maximum amount of players exist in the game.
	 */
	@Test
	public void testTooManyPlayers(){
		for (int i=0; i<Server.MAX_PLAYERS; i++){
			send(i, new ConnectEvent(false));
		}
		IntegrationHelper.findPlayers(server.getGrid(), Server.MAX_PLAYERS);
	}
	
	@Test
	public void testSpectators(){
		send(1, new ConnectEvent(false));
		send(2, new ConnectEvent(false));
		send(3, new ConnectEvent(true));
		IntegrationHelper.findPlayers(server.getGrid(), 2);
	}
	
	private void send(int playerId, Event event){
		event.setPlayerID(playerId);
		server.receive(event);
	}
	
	private void checkPos(Player player, int x, int y){
		assertEquals(new Point(x, y), server.getGrid().find(player));
	}
	
	private void start(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_ENTER)); }
	private void goUp(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_UP)); }
	private void goLeft(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_LEFT)); }
	private void goDown(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_DOWN)); }
	private void goRight(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_RIGHT)); }

}
