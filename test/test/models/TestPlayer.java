package test.models;
import org.junit.*;

import server.controllers.SimulationTimer;
import static org.junit.Assert.*;
import common.models.powerups.BombPlusOnePowerup;
import common.models.powerups.BombRangePowerup;
import common.models.powerups.FlamePassPowerup;
import common.models.powerups.MysteryPowerup;
import common.models.units.Player;

public class TestPlayer {

	Player p;
	
	@Before
	public void setUp(){
		p = new Player(1);
	}
	
	@Test
	public void testBombPlusOnePowerup(){
		p.addPowerup(new BombPlusOnePowerup());
		assertEquals(2, p.getNumBombs());
	}
	
	@Test
	public void testMultipleBombCapacityPowerup(){
		p.addPowerup(new BombPlusOnePowerup());
		assertEquals(2, p.getNumBombs());
		p.addPowerup(new BombPlusOnePowerup());
		assertEquals(3, p.getNumBombs());
	}
	
	@Test
	public void testBombRangePowerup(){
		p.addPowerup(new BombRangePowerup());
		assertEquals(2, p.getBombRange());
	}
	
	@Test
	public void testMultipleBombRangePowerup(){
		p.addPowerup(new BombRangePowerup());
		assertEquals(2, p.getBombRange());
		p.addPowerup(new BombRangePowerup());
		assertEquals(3, p.getBombRange());
	}
	
	@Test
	public void testBombImmunity(){
		p.addPowerup(new FlamePassPowerup(10000));
		assertTrue(p.isImmuneToBombs());
		
		// Test timeout
		SimulationTimer.setTimeCompression(true);
		while(p.isImmuneToBombs());
		assertFalse(p.isImmuneToBombs());
	}
	
	@Test
	public void testInvulnerability(){
		p.addPowerup(new MysteryPowerup(10000));
		assertTrue(p.isInvulnerable());
		
		// Test timeout
		SimulationTimer.setTimeCompression(true);
		while (p.isInvulnerable());
		assertFalse(p.isInvulnerable());
	}
}
