package test.integration;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import org.junit.*;

import common.controllers.GameController.GridBuffer;
import common.events.ConnectRejectedEvent;
import common.models.Grid;
import common.models.Wall;
import common.models.units.LineEnemy;
import common.models.units.PathFindingEnemy;
import common.models.units.Player;

import server.content.CreateGridException;
import server.content.GridLoader;
import server.controllers.SimulationTimer;
import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;

public class TestEnemies {

	private MockServer server;
	private MockClient client;
	private Player player;
	
	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		
		// Connect a client and get the player
		client = new MockClient(true);
		//client.
		List<Player> players = server.getPlayers();
		assertEquals(1, players.size());
		player = players.get(0);
		
		SimulationTimer.setTimeCompression(true);
	}
	
	@After
	public void tearDown(){
		client.stop();
		client.waitFor(ConnectRejectedEvent.class);
		server.stop();
	}
	
	@Test
	public void testPathFindingEnemy() throws InterruptedException{
		Grid testGrid = getTestGrid();
		
		PathFindingEnemy enemy = new PathFindingEnemy();
		testGrid.set(enemy, new Point(1, 3));
		
		Point[] expectedPath = {new Point(2, 3), new Point(2, 2), new Point(3, 2)};
		
		server.newGame(testGrid);
		server.movePlayerTo(player.playerId, new Point(3, 2));
		assertEquals(new Point(3, 2), server.getGridCopy().find(player));
		
		client.startGame();
		
		for(int i = 0; i < expectedPath.length; i++){
			client.waitForViewUpdate();
			assertEquals(expectedPath[i], server.getGridCopy().find(enemy));
		}
	}
	
	@Test
	public void testLineEnemy() throws InterruptedException{
		Grid testGrid = getTestGrid();
		
		LineEnemy enemy = new LineEnemy();
		testGrid.set(new Wall(), new Point(0, 0));
		testGrid.set(enemy, new Point(0, 1));
		
		Point[] expectedPath = {new Point(0, 2), new Point(0, 3)};
		
		server.newGame(testGrid);
		server.movePlayerTo(player.playerId, new Point(3, 2));
		assertEquals(new Point(3, 2), server.getGridCopy().find(player));
		
		client.startGame();
		
		for(int i = 0; i < expectedPath.length; i++){
			client.waitForViewUpdate();
			assertEquals(expectedPath[i], server.getGridCopy().find(enemy));
		}
	}
	
	private Grid getTestGrid(){
		Grid g = null;
		try { g = GridLoader.loadGrid("test/testGrid2.json"); }
		catch (CreateGridException e){ e.printStackTrace(); }
		return g;
	}
}
