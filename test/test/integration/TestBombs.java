package test.integration;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import common.models.Bomb;
import common.models.BombFactory;
import common.models.Player;

public class TestBombs {
	
	private Player player;

	@Before
	public void setUp() throws Exception {
		player = new Player(1);
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

}
