package test.integration;

import static org.junit.Assert.*;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;
import common.models.Bomb;
import common.models.BombPlusOnePowerup;
import common.models.BombRangePowerup;
import common.models.FlamePassPowerup;
import common.models.MysteryPowerup;
import common.models.Player;
import common.models.Powerup;

public class TestPowerups {
	
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
		server.stop();
	}
	
	@Test
	public void testPowerupOnBoard(){
		Powerup bombPlusPowerUp = new BombPlusOnePowerup();
		Point loc = new Point(1,0);
		placePowerup(bombPlusPowerUp, loc);
		assertTrue(server.getGrid().get(loc).contains(bombPlusPowerUp));
	}
	
	@Test
	public void testPickUpPowerup(){
		placePowerup(new BombPlusOnePowerup(), new Point(1,0));
		placePowerup(new BombRangePowerup(), new Point(2,0));
		placePowerup(new FlamePassPowerup(), new Point(3,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertEquals(1, player.getAddedBombs());
		assertEquals(2, player.getNumBombs());
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertEquals(1, player.getAddedBombRange());
		assertEquals(2, player.getBombRange());
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isImmuneToBombs());
		placePowerup(new MysteryPowerup(), new Point(2,0));
		client.pressKeyAndWait(KeyEvent.VK_LEFT);
		assertTrue(player.isInvulnerable());
	}
	
	@Test
	public void testInvulerabilityTimeout(){
		placePowerup(new MysteryPowerup(), new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isInvulnerable());
		// Wait for bomb to detonate
		try {
			Thread.sleep((long) (Player.INVULNERABLE_TIME + 1));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assertFalse(player.isInvulnerable());
	}
	
	@Test
	public void testBombImmunity(){
		placePowerup(new FlamePassPowerup(), new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isImmuneToBombs());
		Bomb bomb = server.dropBombBy(player);
		Point playerLoc = server.getGrid().find(player);
		
		// Wait for bomb to detonate
		try {
			Thread.sleep((long) (Bomb.FUSE_TIME * 1.5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(bomb.isDetonated());
		assertTrue(server.getGrid().hasTypeAt(Player.class, playerLoc));	
	}
	
	@Test
	public void testInvulnerability(){
		placePowerup(new MysteryPowerup(), new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isInvulnerable());
		Bomb bomb = server.dropBombBy(player);
		Point playerLoc = server.getGrid().find(player);
		
		// Wait for bomb to detonate
		try {
			Thread.sleep((long) (Bomb.FUSE_TIME * 1.5));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		assertTrue(bomb.isDetonated());
		assertTrue(server.getGrid().hasTypeAt(Player.class, playerLoc));	
	}
	

	private void placePowerup(Powerup p, Point loc){
		server.getGrid().set(p, loc);
	}
}