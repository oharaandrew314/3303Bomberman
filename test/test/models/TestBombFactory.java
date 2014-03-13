package test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import common.models.Bomb;
import common.models.BombFactory;

public class TestBombFactory {
	
	private BombFactory factory;

	@Before
	public void setUp() throws Exception {
		factory = new BombFactory();
	}
	
	@Test
	public void testUseAllBombs(){
		assertEquals(BombFactory.INIT_MAX_BOMBS, factory.getNumBombs());
		
		// Use all bombs
		for (int i=1; i<=BombFactory.INIT_MAX_BOMBS; i++){
			factory.createBomb();
		}
		assertEquals(0, factory.getNumBombs());
	}
	
	@Test
	public void testBombDetonation(){
		Bomb bomb = factory.createBomb();
		assertEquals(BombFactory.INIT_MAX_BOMBS - 1, factory.getNumBombs());
		bomb.setDetonated();
		assertEquals(BombFactory.INIT_MAX_BOMBS, factory.getNumBombs());
	}
	
	@Test
	public void testIncreaseMaxBombs(){
		factory.increaseMaxBombs();
		assertEquals(BombFactory.INIT_MAX_BOMBS + 1, factory.getNumBombs());
		for (int i=0; i<BombFactory.INIT_MAX_BOMBS + 1; i++){
			assertNotNull(factory.createBomb());
		}
		assertEquals(0, factory.getNumBombs());
	}
	
	@Test(expected=RuntimeException.class)
	public void testCreateTooMany(){
		for (int i=1; i<=BombFactory.INIT_MAX_BOMBS; i++){
			factory.createBomb();
		}
		factory.createBomb();
	}

}
