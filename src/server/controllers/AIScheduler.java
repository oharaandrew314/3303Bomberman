package server.controllers;


import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import common.models.Grid;
import common.models.units.Enemy;
import common.models.units.LineEnemy;
import common.models.units.SmartEnemy;
import common.models.units.RandomEnemy;

public class AIScheduler implements SimulationListener {

	private Random rng;
	private Collection<Enemy> enemies;
	private Server server;
	
	public AIScheduler(Server server){
		rng = new Random();
		enemies = new LinkedList<Enemy>();
		this.server = server;
	}
	
	public synchronized void addEnemy(Enemy enemy){
		enemies.add(enemy);
	}
	public synchronized void removeEnemy(Enemy enemy){
		enemies.remove(enemy);
	}
	
	@Override
	public synchronized void simulationUpdate(long now) {
		for (Enemy enemy : enemies){
			if (enemy.isTimeToMove(now)){
				
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
				else if (enemy instanceof SmartEnemy){
					translation = getPathTranslation(currentLocation);
				}
				else throw new UnsupportedOperationException("Enemy type not supported");
				
				moveEnemy(enemy, translation.x, translation.y);
			}
		}
	}
	
	protected void moveEnemy(Enemy enemy, int dx, int dy){
		server.move(enemy, dx, dy);
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
		List<Point> path = server.getGridCopy().getShortestPath(currentLocation, target);
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
		Grid grid = server.getGridCopy();
		Point currentLocation = getCurrentLocation(enemy);
		List<Point> possibleMoves = new ArrayList<Point>(grid.getPossibleMoves(currentLocation));
		possibleMoves.remove(currentLocation);
		
		// Remove any locations which are occupied by another enemy
		List<Point> toRemove = new ArrayList<>();
		for (Point point : possibleMoves){
			if (grid.hasTypeAt(Enemy.class, point)){
				toRemove.add(point);
			}
		}
		
		if (!toRemove.isEmpty()){
			possibleMoves.removeAll(toRemove);
		}
		return possibleMoves;
	}
	
	private Point getCurrentLocation(Enemy enemy){
		return server.getGridCopy().find(enemy);
	}
	
	@Override
	public synchronized void onTimerReset() {
		enemies.clear();
	}
	
}
