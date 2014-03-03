package test.models;

import org.junit.*;

import common.models.Pillar;
import common.models.Player;
import common.models.Square;
import common.models.Wall;

import static org.junit.Assert.*;

public class TestSquare {
	
	private Square s;
	
	@Before
	public void setUp(){
		s = new Square();
	}
	
	@Test
	public void testAdd(){
		Player p = new Player(1);
		assertTrue(s.add(p));
		assertTrue(s.contains(p));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddTwoImpassables(){
		assertTrue(s.add(new Wall()));
		s.add(new Pillar());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddPlayerToPillar(){
		s.add(new Pillar());
		s.add(new Player(2));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddWallToPillar(){
		s.add(new Pillar());
		s.add(new Wall());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddWallToWall(){
		s.add(new Wall());
		s.add(new Wall());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testAddPillarToPillar(){
		 s.add(new Pillar());
		 s.add(new Pillar());
	}
	
	@Test
	public void testRemove(){
		Player p = new Player(3);
		s.add(p);
		
		assertTrue(s.remove(p));
		assertFalse(s.remove(p));
	}
}
