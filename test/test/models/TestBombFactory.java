package test.models;

import static org.junit.Assert.*;

import java.util.ArrayDeque;
import java.util.Queue;

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
	public void testNormalBehaviour() {
		assertEquals(BombFactory.INIT_MAX_BOMBS, factory.getNumBombs());
		Queue<Bomb> bombs = new ArrayDeque<>();
		
		// Use all bombs
		for (int i=1; i<=BombFactory.INIT_MAX_BOMBS; i++){
			bombs.add(factory.createBomb());
		}
		assertEquals(0, factory.getNumBombs());
		
		// Detonate one bomb
		factory.bombDetonated(bombs.remove());
		assertEquals(1, factory.getNumBombs());
		
		//Increase max bombs by one; should be able to drop two
		factory.increaseMaxBombs();
		assertEquals(2, factory.getNumBombs());
		bombs.add(factory.createBomb());
		bombs.add(factory.createBomb());
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
