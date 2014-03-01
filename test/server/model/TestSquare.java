package server.model;

import org.junit.*;
import static org.junit.Assert.*;

public class TestSquare {
	
	@Test
	public void testAdd(){
		Square s = new Square();
		Player p = new Player("Clark");
		assertTrue(s.add(p));
		assertTrue(s.contains(p));
	}
	
	@Test
	public void testAddTwoImpassables(){
		Square s = new Square();
		assertTrue(s.add(new Box("Impassable 1", true)));
		assertFalse(s.add(new Box("Impassable 2", true)));
	}
	
	@Test
	public void testRemove(){
		Square s = new Square();
		Player p = new Player("Lois");
		s.add(p);
		
		assertTrue(s.remove(p));
		assertFalse(s.remove(p));
	}
}
