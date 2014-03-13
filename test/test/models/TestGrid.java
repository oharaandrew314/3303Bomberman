package test.models;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import common.models.Door;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.Wall;

public class TestGrid {
	
	private MockGrid grid;
	
	/**
	 * Sets up a grid for testing. It should look like this:
	 * 
	 *    -----
	 *    |  .|
	 *    |X1 |
	 *    |   |
	 *    -----
	 */
	@Before
	public void SetupGrid(){
		grid = new MockGrid(new Dimension(3, 3));
		
		grid.set(new Wall(), new Point(2, 0));
		grid.set(new Pillar(), new Point(0, 1));
		grid.set(new Player(1), new Point(1, 1));
		grid.set(new Door(), new Point(2, 2));
	}
	
	//sanity test to make sure other tests are meaningful
	@Test 
	public void testToString(){
		String lineSep = System.getProperty("line.separator");
		assertEquals(
			grid.toString(),
			"-----" + lineSep + "|  .|" + lineSep +"|X1 |"
			+ lineSep + "|   |" + lineSep + "-----"
		);
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
	
	@Test
	public void testPathfindingRange1(){
		Point origin = new Point(1, 0);
		
		Set<Point> expectedPoints = new HashSet<>();
		expectedPoints.add(new Point(2, 0));
		expectedPoints.add(new Point(1, 1));
		expectedPoints.add(origin);
		expectedPoints.add(new Point(0, 0));
		
		assertEquals(expectedPoints, grid.pathfind(origin, 1));
	}
	
	@Test
	public void testPathfindingRange2(){
		Point origin = new Point(1, 0);
		
		Set<Point> expectedPoints = new HashSet<>();
		expectedPoints.add(new Point(2, 0));
		expectedPoints.add(new Point(1, 1));
		expectedPoints.add(origin);
		expectedPoints.add(new Point(0, 0));
		expectedPoints.add(new Point(1, 2));
	
		assertEquals(expectedPoints, grid.pathfind(origin, 2));
	}
	
	@SuppressWarnings("serial")
	private class MockGrid extends Grid {

		public MockGrid(Dimension size) {
			super(size);
		}
		
		public Set<Point> pathfind(Point origin, int range){
			return getPointsInRadius(origin, range, true);
		}
		
	}
}
