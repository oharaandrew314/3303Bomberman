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
		
		grid.set(new Box(true), new Point(2, 0));
		grid.set(new Box(false), new Point(0, 1));
		grid.set(new Player("Peter"), new Point(1, 1));
		grid.set(new Door(), new Point(2, 2));
	}
	
	//sanity test to make sure other tests are meaningful
	@Test 
	public void testToString(){
		assertEquals(grid.toString(), "-----\n|  .|\n|XP |\n|  D|\n-----");
	}
	
	@Test
	public void testFind(){		
		Door door = new Door();
		Point point = new Point(0, 2);
		grid.set(door, point);
		assertEquals(grid.find(door), point);
	}
	
	@Test
	public void testGetPossibleMoves(){
		Set<Square> test = grid.getPossibleMoves(new Point(2, 1));
		assertTrue(test.contains(grid.get(new Point(1, 1))));
		assertTrue(test.contains(grid.get(new Point(2, 2))));
		assertEquals(test.size(), 2);
	}
}
