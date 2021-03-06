package test.integration;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.*;

import common.events.ConnectRejectedEvent;
import common.models.Grid;
import common.models.Wall;
import common.models.units.LineEnemy;
import common.models.units.SmartEnemy;
import common.models.units.Player;
import common.models.units.Enemy;

import server.content.CreateGridException;
import server.content.GridLoader;
import server.controllers.AIScheduler;
import server.controllers.Server;
import server.controllers.SimulationTimer;
import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;

public class TestEnemies {

	private MockServer server;
	private LoggingAIScheduler aiScheduler;
	private MockClient client;
	private Player player;
	private Grid testGrid;
	
	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		aiScheduler = new LoggingAIScheduler(server);
		server.changeAIScheduler(aiScheduler);
		
		try { testGrid = GridLoader.loadGrid("test/testGrid2.json"); }
		catch (CreateGridException e){ e.printStackTrace(); }
		
		// Connect a client and get the player
		client = new MockClient(true);

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
		SmartEnemy enemy = new SmartEnemy();
		testGrid.set(new Wall(), new Point(0, 1));
		testGrid.set(enemy, new Point(0, 2));

		Point[] expectedMoves = {new Point(0, 1), new Point(1, 0), new Point(1, 0), new Point(0, -1)};
		
		server.newGame(testGrid);
		server.movePlayerTo(player.playerId, new Point(3, 2));
		assertEquals(new Point(3, 2), server.getGridCopy().find(player));
		
		client.startGame();
		
		Thread.sleep(2000); //allow enemy to make moves
		
		for(int i = 0; i < expectedMoves.length; i++){
			assertEquals(expectedMoves[i], aiScheduler.moveLog.get(enemy).get(i));
		}
	}
	
	@Test
	public void testLineEnemy() throws InterruptedException{
		LineEnemy enemy = new LineEnemy();
		testGrid.set(new Wall(), new Point(0, 0));
		testGrid.set(enemy, new Point(0, 1));
		
		Point[] expectedMoves = {new Point(0, 1), new Point(0, 1)};
		
		server.newGame(testGrid);
		server.movePlayerTo(player.playerId, new Point(3, 2));
		assertEquals(new Point(3, 2), server.getGridCopy().find(player));
		
		client.startGame();
		
		Thread.sleep(1000); //allow enemy to make moves
		
		for(int i = 0; i < expectedMoves.length; i++){
			assertEquals(expectedMoves[i], aiScheduler.moveLog.get(enemy).get(i));
		}
	}
}

class LoggingAIScheduler extends AIScheduler {

	public Map<Enemy, List<Point>> moveLog;
	public LoggingAIScheduler(Server server) {
		super(server);
		moveLog = new HashMap<Enemy, List<Point>>();
	}
	
	@Override
	protected void moveEnemy(Enemy enemy, int dx, int dy){
		super.moveEnemy(enemy, dx, dy);
		synchronized(moveLog){
			if (!moveLog.containsKey(enemy)){
				List<Point> newMoveList = new ArrayList<Point>();
				newMoveList.add(new Point(dx, dy));
				moveLog.put(enemy, newMoveList);
			}
			else {
				moveLog.get(enemy).add(new Point(dx, dy));
			}
		}
	}
}
