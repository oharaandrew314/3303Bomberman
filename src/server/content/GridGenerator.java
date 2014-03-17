package server.content;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.Pillar;
import common.models.Wall;
import common.models.powerups.BombPlusOnePowerup;
import common.models.powerups.BombRangePowerup;
import common.models.powerups.FlamePassPowerup;
import common.models.powerups.MysteryPowerup;
import common.models.powerups.Powerup;
import common.models.units.Enemy;
import common.models.units.LineEnemy;
import common.models.units.PathFindingEnemy;
import common.models.units.RandomEnemy;

public class GridGenerator {
	
	private final Random r;
	private final Grid grid;
	
	private static final double
		WALL_DENSITY = 0.3,
		ENEMY_DENSITY = 0.04,
		POWERUP_DENSITY = 0.02;

	private GridGenerator(Dimension size, Random r){
		this.r = r;
		grid = new Grid(size);
		
		List<Point> points = new ArrayList<>(grid.keySet());
		
		// Create Pillars
		for (Point point : points){
			if (grid.isPassable(point) && pillarLoc(point)){
				grid.set(new Pillar(), point);
			}
		}
		
		// Create walls
		for (int i=0; i<getNum(WALL_DENSITY); i++){
			findPlace(points, new Wall(), false);
		}
		
		// Create Enemies
		for (int i=0; i<getNum(ENEMY_DENSITY); i++){
			findPlace(points, randomEnemy(), false);
		}
		
		// Create Powerups
		for (int i=0; i<getNum(POWERUP_DENSITY); i++){
			findPlace(points, randomPowerup(), false);
		}
		
		// Place Door underneath a random wall
		findPlace(points, new Door(), true);
	}
	
	private int getNum(double density){
		int area = grid.getSize().height * grid.getSize().width;
		return (int) (area * density);
	}
	
	private void findPlace(List<Point> points, Entity entity, boolean underWall){
		boolean placed = false;
		while (!placed){
			Point p = points.get(r.nextInt(points.size()));
			if ((underWall && grid.hasTypeAt(Wall.class, p)) || grid.isPassable(p)){
				placed = grid.set(entity, p);
			}
		}
	}
	
	private Enemy randomEnemy(){
		switch(r.nextInt(3)){
		case 0: return new RandomEnemy();
		case 1: return new LineEnemy();
		case 2: return new PathFindingEnemy();
		default: throw new RuntimeException("Invalid random enemy");
		}
	}
	
	private Powerup randomPowerup(){
		switch(r.nextInt(4)){
		case 0: return new BombPlusOnePowerup();
		case 1: return new BombRangePowerup();
		case 2: return new FlamePassPowerup();
		case 3: return new MysteryPowerup();
		default: throw new RuntimeException("Invalid random powerup");
		}
	}
	
	private boolean pillarLoc(Point point){
		return point.x % 2 == 1 && point.y %2 == 1;
	}
	
	// Generator functions

	public static Grid createRandomGrid(Dimension size){
		return new GridGenerator(size, new Random()).grid;
	}
	
	public static Grid createRandomGrid(Dimension size, long seed){
		return new GridGenerator(size, new Random(seed)).grid;
	}
}
