package server.controllers;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.models.Door;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.Wall;

public class GridGenerator {
	
	private final Random r;
	private final Grid grid;
	
	private static final double WALL_DENSITY = 0.7;
	
	private GridGenerator(Dimension size, int numPlayers, Random r){
		this.r = r;
		grid = new Grid(size);
		
		List<Point> points = new ArrayList<>(grid.keySet());
		
		// Create Pillars
		for (Point point : points){
			if (grid.isPassable(point) && pillarLoc(point)){
				grid.set(new Pillar(), point);
			}
		}
		
		// Create random walls
		for (Point point : points){
			if (chance(WALL_DENSITY) && grid.isPassable(point)){
				grid.set(new Wall(), point);
			}
		}
		
		// Place Door
		boolean placed = false;
		while (!placed){
			Point p = points.get(r.nextInt(points.size()));
			placed = grid.set(new Door(), p);
		}
		
		// Place Player
		int playersPlaced = 0;
		while (playersPlaced < numPlayers){
			Point p = points.get(r.nextInt(points.size()));
			if (grid.isPassable(p)){
				grid.set(new Player("Player " + ++playersPlaced), p);
			}
		}
	}
	
	private boolean chance(double chance){
		return r.nextDouble() > chance;
	}
	
	private boolean pillarLoc(Point point){
		return point.x % 2 == 1 && point.y %2 == 1;
	}
	
	// Generator functions

	public static Grid createRandomGrid(Dimension size, int numPlayers){		
		return new GridGenerator(size, numPlayers, new Random()).grid;
	}
	
	public static Grid createRandomGrid(Dimension size, int numPlayers, long seed){
		return new GridGenerator(size, numPlayers, new Random(seed)).grid;
	}
}
