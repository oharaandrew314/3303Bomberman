package test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import server.content.GridGenerator;

import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.Pillar;
import common.models.Wall;

public class TestGridGenerator {
	
	private static final int MAX_DOORS = 1;
	
	@Test
	public void testRandom1(){
		int seed = 123456789;
		Grid grid = GridGenerator.createRandomGrid(new Dimension(4, 4), seed);
		testGrid(grid);
	}
	
	@Test
	public void testRandom2(){
		int seed = 987654321;
		Grid grid = GridGenerator.createRandomGrid(new Dimension(4, 6), seed);
		testGrid(grid);
	}
	
	@Test
	public void testRandom3(){
		int seed = 135792468;
		Grid grid = GridGenerator.createRandomGrid(new Dimension(3, 5), seed);
		testGrid(grid);
	}
	
	@Test
	public void testRandom4(){
		int seed = 135792468;
		Grid grid = GridGenerator.createRandomGrid(
			new Dimension(10, 10), seed
		);
		testGrid(grid);
	}

	
	// Helpers
	
	private void testGrid(Grid grid){
		testPillars(grid);
		testWalls(grid);
		testDoor(grid);
	}
	
	private void testWalls(Grid grid) {
		int numWalls = 0;
		for (Entity entity : getEntities(grid)){
			if (entity instanceof Wall){
				numWalls++;
			}
		}
		
		assertTrue(numWalls > 0);
	}
	
	private void testDoor(Grid grid){
		int numDoors = 0;
		for (Entity entity : getEntities(grid)){
			if (entity instanceof Door){
				numDoors++;
			}
		}
		assertEquals(numDoors, MAX_DOORS);
	}
	
	/**
	 * No matter the wa the grid is generated; the pillars must always form a
	 * set pattern.  This tests and asserts that pattern.
	 * @param grid the grid to test the pillar pattern of
	 */
	private void testPillars(Grid grid){
		for (int j=0; j<grid.getSize().height; j+=2){
			for (int i=0; i<grid.getSize().width; i+=2){
				assertHasPillar(grid, new Point(i, j), false);
			}
		}
		
		for (int j=1; j<grid.getSize().height; j+=2){
			for (int i=1; i<grid.getSize().width; i+=2){
				assertHasPillar(grid, new Point(i, j), true);
			}
		}
	}
	
	private void assertHasPillar(Grid grid, Point point, boolean needsPillar){		
		// Check if square has a pillar
		boolean hasPillar = false;
		for (Entity entity : grid.get(point)){
			if (entity instanceof Pillar){
				hasPillar = true;
			}
		}
		
		assertEquals(hasPillar, needsPillar);
	}
	
	private Set<Entity> getEntities(Grid grid){
		Set<Entity> entities = new HashSet<>();
		for (Point point : grid.keySet()){
			entities.addAll(grid.get(point));
		}
		return entities;
	}
}
