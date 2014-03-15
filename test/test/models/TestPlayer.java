package test.models;
import org.junit.*;

import static org.junit.Assert.*;
import common.models.BombPlusOnePowerup;
import common.models.BombRangePowerup;
import common.models.FlamePassPowerup;
import common.models.MysteryPowerup;
import common.models.Player;

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
	}
	
	@Test
	public void testInvulnerability(){
		p.addPowerup(new MysteryPowerup(10000));
		assertTrue(p.isInvulnerable());
	}
	
	@Test
	public void testInvulnerabilityTimeOut(){
		p.addPowerup(new MysteryPowerup(10000));
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (10000);
		while(currentTime < endTime){
			currentTime = System.currentTimeMillis();
		}
		assertFalse(p.isInvulnerable());
	}
	
	@Test
	public void testInvulnerabilityAfter5Seconds(){
		p.addPowerup(new MysteryPowerup(10000));
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (5000);
		while(currentTime < endTime){
			currentTime = System.currentTimeMillis();
		}
		assertTrue(p.isInvulnerable());
	}
	
	@Test
	public void testFlamePassTimeOut(){
		p.addPowerup(new FlamePassPowerup(10000));
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (10000);
		while(currentTime < endTime){
			currentTime = System.currentTimeMillis();
		}
		assertFalse(p.isImmuneToBombs());
	}
	
	@Test
	public void testFlamePassAfter5Seconds(){
		p.addPowerup(new FlamePassPowerup(10000));
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (5000);
		while(currentTime < endTime){
			currentTime = System.currentTimeMillis();
		}
		assertTrue(p.isImmuneToBombs());
	}
	
}
