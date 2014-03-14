package test.models;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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
	
	public static final String
		LINE_SEP = System.getProperty("line.separator"),
		GRID_STRING = (
			"-----" + LINE_SEP +
			"|  .|" + LINE_SEP +
			"|X1 |" + LINE_SEP +
			"|  D|" + LINE_SEP +
			"-----"
		);
	
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
		assertEquals(GRID_STRING, grid.toString());
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
		Set<Point> actual = grid.getPossibleMoves(new Point(2, 1));
		Point[] expected = new Point[]{
			new Point(2, 2), new Point(2, 1), new Point(1, 1)
		};
		assertArrayEquals(expected, actual.toArray());
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
	
	@Test
	public void testPathfindingRange2WithBlockingBox(){
		Point origin = new Point(2, 2);
		
		/* Put wall directly next to wall.
		 * Explosion of range 2 should not go past it */
		grid.set(new Wall(), new Point(1, 2));
		
		Set<Point> expectedPoints = new HashSet<>();
		expectedPoints.add(new Point(1, 2));
		expectedPoints.add(new Point(2, 1));
		expectedPoints.add(new Point(2, 0));
		expectedPoints.add(origin);
		
		assertEquals(expectedPoints, grid.pathfind(origin, 2));
	}
	
	@Test
	public void testWallCoveringDoor(){
		Point doorLoc = new Point(2, 2);
		Wall wall = new Wall();
		grid.set(wall, doorLoc);
		
		String expectedGridString = new String(GRID_STRING).replace("D", wall.toString());
		assertEquals(expectedGridString, grid.toString());
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
