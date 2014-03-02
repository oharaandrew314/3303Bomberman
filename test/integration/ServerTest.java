package integration;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.Server;

import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.models.Entity;
import common.models.Grid;
import common.models.Player;

public class ServerTest {
	
	private Server server;
	private Grid grid;

	@Before
	public void setUp() throws Exception {
		grid = GridLoader.loadGrid("grid1.json");
		server = new Server(grid);
	}

	@Test
	public void testOnePlayer() {
		// Connect one player
		send(1, new ConnectEvent());
		
		// Ensure Player starts at (0, 0)
		List<Player> players = findPlayers(1);
		Player p = players.get(0);
		grid.remove(p);
		grid.set(p, new Point(0, 0));
		
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
	}
	
	@Test
	public void testTwoPlayers(){
		// Connect one player
		send(1, new ConnectEvent());
		send(2, new ConnectEvent());
		
		List<Player> players = findPlayers(2);
		Player p1 = players.get(0);
		grid.remove(p1);
		grid.set(p1, new Point(0, 0));
		
		Player p2 = players.get(1);
		grid.remove(p2);
		grid.set(p2, new Point(2, 1));
		
		//Move p1
		goRight(p1);
		checkPos(p1, 1, 0);
		
		// Move p2
		goUp(p2);
		checkPos(p2, 2, 0);
		
		// Collide
		goLeft(p2);
		findPlayers(0);
		
	}
	
	/**
	 * This test creates one too many players, and then ensures that only
	 * the maximum amount of players exist in the game.
	 */
	@Test
	public void testTooManyPlayers(){
		for (int i=0; i<Server.MAX_PLAYERS; i++){
			send(i, new ConnectEvent());
		}
		findPlayers(Server.MAX_PLAYERS);
	}
	
	private void send(int playerId, Event event){
		event.setPlayerID(playerId);
		server.receive(event);
		server.simulationUpdate();
	}
	
	private List<Player> findPlayers(int numPlayers){
		List<Player> players = new ArrayList<>();
		for (Point point : grid.keySet()){
			for (Entity entity: grid.get(point)){
				if (entity instanceof Player){
					players.add((Player) entity);
				}
			}
		}
		assertEquals(numPlayers, players.size());
		return players;
	}
	
	private void checkPos(Player player, int x, int y){
		assertEquals(new Point(x, y), grid.find(player));
	}
	
	private void goUp(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_UP)); }
	private void goLeft(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_LEFT)); }
	private void goDown(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_DOWN)); }
	private void goRight(Player p) { send(p.playerId, new GameKeyEvent(KeyEvent.VK_RIGHT)); }

}
