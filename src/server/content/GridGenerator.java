package server.content;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import common.models.Door;
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
		ENEMY_DENSITY = 0.08,
		POWERUP_DENSITY = 0.04;

	private GridGenerator(Dimension size, Random r){
		this.r = r;
		grid = new Grid(size);
		
		List<Point> points = new ArrayList<>(grid.keySet());
		
		// Create Pillars and walls
		for (Point point : points){
			if (grid.isPassable(point) && pillarLoc(point)){
				grid.set(new Pillar(), point);
			}
			else if (chance(WALL_DENSITY) && grid.isPassable(point)){
				grid.set(new Wall(), point);
			}
			else if (chance(ENEMY_DENSITY) && grid.isPassable(point)){
				grid.set(randomEnemy(r), point);
			}
			else if (chance(POWERUP_DENSITY) && grid.isPassable(point)){
				grid.set(randomPowerup(r), point);
			}
		}
		
		// Place Door underneath a random wall
		boolean placed = false;
		while (!placed){
			Point p = points.get(r.nextInt(points.size()));
			if (grid.hasTypeAt(Wall.class, p)){
				placed = grid.set(new Door(), p);
			}
		}
	}
	
	private boolean chance(double density){
		return r.nextDouble() < density;
	}
	
	private Enemy randomEnemy(Random r){
		switch(r.nextInt(3)){
		case 0: return new RandomEnemy();
		case 1: return new LineEnemy();
		case 2: return new PathFindingEnemy();
		default: throw new RuntimeException("Invalid random enemy");
		}
	}
	
	private Powerup randomPowerup(Random r){
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
