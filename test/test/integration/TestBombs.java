package test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;

import common.models.Bomb;
import common.models.BombFactory;
import common.models.Player;

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
	}
	
	@After
	public void tearDown(){
		server.stop();
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
			bombs[i].detonate();
			assertEquals(i + 1, player.getNumBombs());
		}
	}
	
	@Test
	public void testBombDeployPosition(){
		
	}
	
	@Test
	public void testTimedDetonation(){
		Bomb bomb = server.bomb(player);
		assertTrue(!bomb.isDetonated());
		assertEquals(BombFactory.INIT_MAX_BOMBS - 1, player.getNumBombs());
		
		// Wait for bomb to detonate
		try {
			Thread.sleep((long) (Bomb.FUSE_TIME * 1.5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(bomb.isDetonated());
		assertEquals(BombFactory.INIT_MAX_BOMBS, player.getNumBombs());
	}

}
