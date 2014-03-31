package test.integration;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.controllers.SimulationTimer;
import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;
import common.events.ConnectRejectedEvent;
import common.models.Bomb;
import common.models.BombFactory;
import common.models.Grid;
import common.models.Wall;
import common.models.units.Player;

public class TestBombs {
	
	private MockServer server;
	private MockClient client;
	private Player player;

	@Before
	public void setUp() throws Exception {
		server = new MockServer();
		
		// Connect a client and get the player
		client = new MockClient(true);
		List<Player> players = server.getPlayers();
		assertEquals(1, players.size());
		player = players.get(0);
		
		// Start game (and simulation timer)
		server.newGame();
		client.startGame();
		
		// move player to top left
		server.movePlayerTo(player.playerId, new Point(0, 0));
	}
	
	@After
	public void tearDown(){
		client.stop();
		client.waitFor(ConnectRejectedEvent.class);
		server.stop();
		SimulationTimer.setTimeCompression(false);
	}

	@Test
	public void testOneBomb() {
		assertEquals(BombFactory.INIT_MAX_BOMBS, player.getNumBombs());
		player.getNextBomb();
		assertEquals(BombFactory.INIT_MAX_BOMBS - 1, player.getNumBombs());
	}
	
	@Test
	public void testBombReplacement(){
		final Bomb[] bombs = new Bomb[BombFactory.INIT_MAX_BOMBS];
		for (int i=0; i<BombFactory.INIT_MAX_BOMBS; i++){
			bombs[i] = player.getNextBomb();
		}
		assertEquals(0, player.getNumBombs());
		
		for (int i=0; i<BombFactory.INIT_MAX_BOMBS; i++){
			bombs[i].setDetonated();
			assertEquals(i + 1, player.getNumBombs());
		}
	}
	
	@Test
	public void testBombDeployPosition(){
		// Drop bomb and do not move
		Bomb bomb = server.dropBombBy(player);
		Grid grid = server.getGridCopy();
		assertEquals(grid.find(player), grid.find(bomb));
	}
	
	@Test
	public void testBombDeployPositionAfterMove(){
		Bomb bomb = server.dropBombBy(player);
		
		Point dropLoc = server.getGridCopy().find(player);
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		
		Grid afterGrid = server.getGridCopy();
		
		assertEquals(dropLoc, afterGrid.find(bomb));
		assertNotEquals(afterGrid.find(player), afterGrid.find(bomb));
	}
	
	@Test
	public void testTimedDetonation(){
		Point playerLoc = server.getGridCopy().find(player);
		Bomb bomb = server.dropBombBy(player);
		assertTrue(!bomb.isDetonated());
		assertEquals(BombFactory.INIT_MAX_BOMBS - 1, player.getNumBombs());
		assertTrue(server.getGridCopy().hasTypeAt(Bomb.class, playerLoc));
		
		// Wait for bomb to detonate
		SimulationTimer.setTimeCompression(true);
		while(!bomb.isDetonated());
		client.waitForViewUpdate();
		
		assertTrue(!server.getGridCopy().hasTypeAt(Bomb.class, playerLoc));
		assertTrue(bomb.isDetonated());
		assertEquals(BombFactory.INIT_MAX_BOMBS, player.getNumBombs());
	}
	
	@Test
	public void testDropUnavailableBomb(){
		Bomb bomb = null;
		for (int i=1; i<=BombFactory.INIT_MAX_BOMBS; i++){
			bomb = server.dropBombBy(player);
			assertNotNull(bomb);
			assertEquals(BombFactory.INIT_MAX_BOMBS - i, player.getNumBombs());
		}
		assertEquals(0, player.getNumBombs());
		bomb = server.dropBombBy(player);
		assertNull(bomb);
	}
	
	@Test
	public void testDestroyObjects(){
		Point playerLoc = new Point(0, 2);
		server.movePlayerTo(player.playerId, playerLoc);
		
		// test if wall starts where it's supposed to (sanity test)
		Point wallPoint = new Point(1, 2);
		assertTrue(server.getGridCopy().hasTypeAt(Wall.class, wallPoint));
		
		// Drop bomb
		Bomb bomb = server.dropBombBy(player);
		server.detonateBomb(bomb);
		
		Grid afterDetonation = server.getGridCopy();
		// Make sure wall is gone now
		assertTrue(!afterDetonation.hasTypeAt(Wall.class, wallPoint));
		
		// Make sure player is dead, too (not just removed from grid)
		assertTrue(!afterDetonation.hasTypeAt(Player.class, playerLoc));
		assertTrue(server.getPlayers().isEmpty());
	}
	
	@Test
	public void testChainReaction(){
		// allow player to drop two bombs
		player.increaseMaxBombs();
		
		// Place two bombs next to each-other
		server.movePlayerTo(player.playerId, new Point(0, 0));
		Bomb b1 = server.dropBombBy(player);
		server.movePlayerTo(player.playerId, new Point(1, 0));
		Bomb b2 = server.dropBombBy(player);
		assertNotNull(b1);
		assertNotNull(b2);
		
		// detonate
		server.detonateBomb(b1);
		
		// Ensure chain reaction occured
		assertTrue(b1.isDetonated());
		assertTrue(b2.isDetonated());
	}
}
