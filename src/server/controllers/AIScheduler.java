package server.controllers;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;

import common.models.Enemy;
import common.models.PathFindingEnemy;
import common.models.LineEnemy;
import common.models.RandomEnemy;

public class AIScheduler implements SimulationListener {

	public static final int ENEMY_MOVE_FREQ = 1200;
	private Random rng;
	private Map<Enemy, Long> enemies;
	private Server server;
	
	public AIScheduler(Server server){
		rng = new Random();
		enemies = new HashMap<Enemy, Long>();
		this.server = server;
	}
	
	public synchronized void addEnemy(Enemy enemy){
		enemies.put(enemy, 0L);
	}
	public synchronized void removeEnemy(Enemy enemy){
		enemies.remove(enemy);
	}
	
	@Override
	public synchronized void simulationUpdate(long now) {
		for (Enemy enemy : enemies.keySet()){
			if (now - enemies.get(enemy) > ENEMY_MOVE_FREQ){
				
				Point translation;
				Point currentLocation = getCurrentLocation(enemy);
				List<Point> possibleMoves = getPossibleMoves(enemy);
				
				if (possibleMoves.isEmpty()) {
					translation = new Point(0, 0);
				}
				else if (enemy instanceof RandomEnemy){
					translation = getRandomTranslation(currentLocation, possibleMoves);
				}
				else if (enemy instanceof LineEnemy){
					LineEnemy le = (LineEnemy) enemy;
					translation = getLineTranslation(le.getDirection(), currentLocation, possibleMoves);
					le.setDirection(translation); // save possibly new direction
				}
				else if (enemy instanceof PathFindingEnemy){
					translation = getPathTranslation(currentLocation);
				}
				else throw new UnsupportedOperationException("Enemy type not supported");
				
				enemies.put(enemy, now); //register new movement time
				server.move(enemy, translation.x, translation.y);
			}
		}
	}
	
	private Point getRandomTranslation(Point currentLocation, List<Point> possibleMoves){
		Point nextMove = possibleMoves.get(rng.nextInt(possibleMoves.size()));
		return new Point(nextMove.x - currentLocation.x, nextMove.y - currentLocation.y);
	}
	
	private Point getLineTranslation(Point currentDirection, Point currentLocation, List<Point> possibleMoves){
		Point translation = currentDirection;
		
		// if can no longer move in current direction, get new direction
		if (!possibleMoves.contains(new Point(currentLocation.x + translation.x, currentLocation.y + translation.y))){
			translation = getRandomTranslation(currentLocation, possibleMoves);
		}
		return translation;
	}
	
	private Point getPathTranslation(Point currentLocation){
		// no need for possible moves in this method, as shortest path calculation
		// will figure that out anyways
		Point target = server.getNearestPlayerLocation(currentLocation);
		
		if (target == null) return new Point(0, 0);
		List<Point> path = server.getGrid().getShortestPath(currentLocation, target);
		if (path == null || path.size() <= 1) return new Point(0, 0);
		
		Point nextMove = path.get(1); //current location in path, want next point
		return new Point(nextMove.x - currentLocation.x, nextMove.y - currentLocation.y);
	}
	
	/**
	 * Gets the possible locations this enemy can move to, other than the null move
	 * @param enemy The enemy
	 * @return The list of points the enemy can move to. 
	 * A list rather than a set to make random indexing easier.
	 */
	private List<Point> getPossibleMoves(Enemy enemy){
		Point currentLocation = getCurrentLocation(enemy);
		List<Point> possibleMoves = new ArrayList<Point>(server.getGrid().getPossibleMoves(currentLocation));
		possibleMoves.remove(currentLocation);
		return possibleMoves;
	}
	
	private Point getCurrentLocation(Enemy enemy){
		return server.getGrid().find(enemy);
	}
	
	@Override
	public void onTimerReset() {
		enemies.clear();
	}
	
}
