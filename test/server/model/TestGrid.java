package server.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class TestGrid {
	
	private Grid grid;
	
	/**
	 * Sets up a grid for testing. It should look like this:
	 * 
	 *    -----
	 *    |  .|
	 *    |XP |
	 *    |  D|
	 *    -----
	 */
	@Before
	public void SetupGrid(){
		grid = new Grid(new Dimension(3, 3));
		Square s = new Square();
		
		grid.set(new Square(), new Point(0, 0));
		grid.set(new Square(), new Point(1, 0));
		
		s = new Square();
		s.add(new Box("Box", true));
		grid.set(s, new Point(2, 0));
		
		s = new Square();
		s.add(new Box("Box", false));		
		grid.set(s, new Point(0, 1));
		
		s = new Square();
		s.add(new Player("Peter"));
		grid.set(s, new Point(1, 1));
		
		grid.set(new Square(), new Point(2, 1));
		grid.set(new Square(), new Point(0, 2));
		grid.set(new Square(), new Point(1, 2));
		
		s = new Square();
		s.add(new Door("Door"));
		grid.set(s, new Point(2, 2));
	}
	
	//sanity test to make sure other tests are meaningful
	@Test 
	public void testToString(){
		assertEquals(grid.toString(), "-----\n|  .|\n|XP |\n|  D|\n-----");
	}
	
	@Test
	public void testFind(){
		Square s = new Square();
		grid.set(s, new Point(0, 2));
		assertEquals(grid.find(s), new Point(0, 2));
	}
	
	@Test
	public void testGetPossibleMoves(){
		Set<Square> test = grid.getPossibleMoves(new Point(2, 1));
		assertTrue(test.contains(grid.get(new Point(1, 1))));
		assertTrue(test.contains(grid.get(new Point(2, 2))));
		assertEquals(test.size(), 2);
	}
}
