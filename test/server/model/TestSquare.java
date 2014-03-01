package server.model;

import org.junit.*;
import static org.junit.Assert.*;

public class TestSquare {
	
	private Square s;
	
	@Before
	public void setUp(){
		s = new Square();
	}
	
	@Test
	public void testAdd(){
		Player p = new Player("Clark");
		assertTrue(s.add(p));
		assertTrue(s.contains(p));
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddTwoImpassables(){
		assertTrue(s.add(new Wall()));
		s.add(new Pillar());
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddPlayerToPillar(){
		s.add(new Pillar());
		s.add(new Player("Pete"));
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddWallToPillar(){
		s.add(new Pillar());
		s.add(new Wall());
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddWallToWall(){
		s.add(new Wall());
		s.add(new Wall());
	}
	
	@Test(expected=RuntimeException.class)
	public void testAddPillarToPillar(){
		 s.add(new Pillar());
		 s.add(new Pillar());
	}
	
	@Test
	public void testRemove(){
		Player p = new Player("Lois");
		s.add(p);
		
		assertTrue(s.remove(p));
		assertFalse(s.remove(p));
	}
}
