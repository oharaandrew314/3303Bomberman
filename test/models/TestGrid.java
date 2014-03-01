package models;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.awt.Dimension;
import java.awt.Point;

import org.junit.Before;
import org.junit.Test;

import common.models.Door;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.Wall;

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
		
		grid.set(new Wall(), new Point(2, 0));
		grid.set(new Pillar(), new Point(0, 1));
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
		Object[] actual = grid.getPossibleMoves(new Point(2, 1)).toArray();
		Point[] expected = new Point[]{new Point(2, 2), new Point(1, 1)};
		assertArrayEquals(actual, expected);
	}
}
