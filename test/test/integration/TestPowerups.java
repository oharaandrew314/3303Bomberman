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

import server.controllers.SimulationTimer;
import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;

import common.controllers.GameController.GridBuffer;
import common.models.Bomb;
import common.models.powerups.BombPlusOnePowerup;
import common.models.powerups.BombRangePowerup;
import common.models.powerups.FlamePassPowerup;
import common.models.powerups.MysteryPowerup;
import common.models.powerups.Powerup;
import common.models.units.Player;

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
		SimulationTimer.setTimeCompression(false);
		client.stop();
		server.stop();
	}
	
	@Test
	public void testPowerupOnBoard(){
		Powerup bombPlusPowerUp = new BombPlusOnePowerup();
		Point loc = new Point(1,0);
		placePowerup(bombPlusPowerUp, loc);
		assertTrue(server.getGridCopy().get(loc).contains(bombPlusPowerUp));
	}
	
	@Test
	public void testPickUpPowerup(){
		placePowerup(new BombPlusOnePowerup(), new Point(1,0));
		placePowerup(new BombRangePowerup(), new Point(2,0));
		placePowerup(new FlamePassPowerup(10000), new Point(3,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertEquals(2, player.getNumBombs());
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertEquals(2, player.getBombRange());
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isImmuneToBombs());
		placePowerup(new MysteryPowerup(10000), new Point(2,0));
		client.pressKeyAndWait(KeyEvent.VK_LEFT);
		assertTrue(player.isInvulnerable());
	}
	
	@Test
	public void testInvulerabilityTimeout(){
		MysteryPowerup invulnerability = new MysteryPowerup(5000);
		placePowerup(invulnerability, new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isInvulnerable());
		// Wait for invulnerability to expire
		SimulationTimer.setTimeCompression(true);
		while(player.isInvulnerable());
		assertFalse(player.isInvulnerable());
	}
	
	@Test 
	public void testFlamePassTimeout(){
		FlamePassPowerup flamePass = new FlamePassPowerup(10000);
		placePowerup(flamePass, new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isImmuneToBombs());

		// wait for flamepass to expire
		SimulationTimer.setTimeCompression(true);
		while(player.isImmuneToBombs());
		assertFalse(player.isImmuneToBombs());
	}
	
	@Test
	public void testBombImmunity(){
		placePowerup(new FlamePassPowerup(10000), new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isImmuneToBombs());
		Bomb bomb = server.dropBombBy(player);
		Point playerLoc = server.getGridCopy().find(player);
		
		// Wait for bomb to detonate
		SimulationTimer.setTimeCompression(true);
		while(!bomb.isDetonated());
		
		assertTrue(bomb.isDetonated());
		client.waitForViewUpdate();
		assertTrue(server.getGridCopy().hasTypeAt(Player.class, playerLoc));	
	}
	
	@Test
	public void testInvulnerability(){
		placePowerup(new MysteryPowerup(10000), new Point(1,0));
		client.pressKeyAndWait(KeyEvent.VK_RIGHT);
		assertTrue(player.isInvulnerable());
		Bomb bomb = server.dropBombBy(player);
		Point playerLoc = server.getGridCopy().find(player);
		
		// Wait for bomb to detonate
		SimulationTimer.setTimeCompression(true);
		while(!bomb.isDetonated());
		
		client.waitForViewUpdate();
		assertTrue(bomb.isDetonated());
		assertTrue(server.getGridCopy().hasTypeAt(Player.class, playerLoc));	
	}
	

	private void placePowerup(Powerup p, Point loc){
		try(GridBuffer buf = server.acquireGrid()){
			buf.grid.set(p, loc);
		}
	}
}
