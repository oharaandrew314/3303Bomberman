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
		p.addPowerup(new BombPlusOnePowerup("B"));
		assertEquals(1, p.getAddedBombs());
	}
	
	@Test
	public void testMultipleBombCapacityPowerup(){
		p.addPowerup(new BombPlusOnePowerup("B"));
		assertEquals(1, p.getAddedBombs());
		p.addPowerup(new BombPlusOnePowerup("B"));
		assertEquals(2, p.getAddedBombs());
	}
	
	@Test
	public void testBombRangePowerup(){
		p.addPowerup(new BombRangePowerup("R"));
		assertEquals(1, p.getAddedBombRange());
	}
	
	@Test
	public void testMultipleBombRangePowerup(){
		p.addPowerup(new BombRangePowerup("R"));
		assertEquals(1, p.getAddedBombRange());
		p.addPowerup(new BombRangePowerup("R"));
		assertEquals(2, p.getAddedBombRange());
	}
	
	@Test
	public void testBombImmunity(){
		p.addPowerup(new FlamePassPowerup("F"));
		assertTrue(p.isImmuneToBombs());
	}
	
	@Test
	public void testInvulnerability(){
		p.addPowerup(new MysteryPowerup("M"));
		assertTrue(p.isInvulnerable());
	}
	
	@Test
	public void testInvulnerabilityTimeOut(){
		p.addPowerup(new MysteryPowerup("M"));
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (1000*10);
		while(currentTime < endTime){
			currentTime = System.currentTimeMillis();
		}
		assertFalse(p.isInvulnerable());
	}
	
	@Test
	public void testInvulnerabilityAfter5Seconds(){
		p.addPowerup(new MysteryPowerup("M"));
		long currentTime = System.currentTimeMillis();
		long endTime = currentTime + (1000*5);
		while(currentTime < endTime){
			currentTime = System.currentTimeMillis();
		}
		assertTrue(p.isInvulnerable());
	}
	
}
