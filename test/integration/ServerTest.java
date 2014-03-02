package integration;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.event.KeyEvent;

import org.junit.Before;
import org.junit.Test;

import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.models.Entity;
import common.models.Grid;
import common.models.Player;
import server.content.GridLoader;
import server.controllers.Server;

public class ServerTest {
	
	private static final int PLAYER_ID = 1;
	
	private Server server;
	private Grid grid;
	private Player player;

	@Before
	public void setUp() throws Exception {
		grid = GridLoader.loadGrid("grid1.json");
		server = new Server(grid);
	}

	@Test
	public void testOnePlayer() {
		send(new ConnectEvent());
		
		// Ensure Player starts at (0, 0)
		findPlayer();
		grid.set(player, new Point(0, 0));
		
		goUp();  // Try going out of bounds
		checkPos(0, 0);
		
		goDown();  // Try going to empty space
		checkPos(0, 1);
		
		goRight();  // Try going into pillar
		checkPos(0, 1);
		
		goDown();
		checkPos(0, 2);
		
		goRight(); // Try going into wall
		checkPos(0, 2);
		
		goDown();
		checkPos(0, 3);
		
		goRight();
		checkPos(1, 3);
		
		goLeft();
		checkPos(0, 3);
		
		goUp();
		checkPos(0, 2);
		
	}
	
	private void send(Event event){
		event.setPlayerID(PLAYER_ID);
		server.receive(event);
	}
	
	private void findPlayer(){
		for (Point point : grid.keySet()){
			for (Entity entity: grid.get(point)){
				if (entity instanceof Player){
					player = (Player) entity;
				}
			}
		}
		assertNotNull(player);
	}
	
	private void checkPos(int x, int y){
		assertEquals(new Point(x, y), grid.find(player));
	}
	
	private void goUp() { send(new GameKeyEvent(KeyEvent.VK_UP)); }
	private void goLeft() { send(new GameKeyEvent(KeyEvent.VK_LEFT)); }
	private void goDown() { send(new GameKeyEvent(KeyEvent.VK_DOWN)); }
	private void goRight() { send(new GameKeyEvent(KeyEvent.VK_RIGHT)); }

}
