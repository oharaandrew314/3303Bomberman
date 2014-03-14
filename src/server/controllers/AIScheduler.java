package server.controllers;


import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.List;

import common.models.Direction;
import common.models.Enemy;
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
	public synchronized void simulationUpdate() {
		for (Enemy enemy : enemies.keySet()){
			if (System.currentTimeMillis() - enemies.get(enemy) > ENEMY_MOVE_FREQ){
				Point translation;
				if (enemy instanceof RandomEnemy){
					translation = getRandomTranslation((RandomEnemy)enemy);
				}
				else throw new UnsupportedOperationException("Other enemies not supported yet");
				
				enemies.put(enemy, System.currentTimeMillis()); //register new movement time
				server.move(enemy, translation.x, translation.y);
			}
		}
	}
	
	private Point getRandomTranslation(RandomEnemy enemy){
		Point currentLocation = server.getGrid().find(enemy);
		List<Point> possibleMoves = new ArrayList<Point>(server.getGrid().getPossibleMoves(currentLocation));
		if (possibleMoves.isEmpty()) return new Point(0, 0);
		
		Point nextMove = possibleMoves.get(rng.nextInt(possibleMoves.size()));
		return new Point(nextMove.x - currentLocation.x, nextMove.y - currentLocation.y);
	}
	
	@Override
	public void onTimerReset() {
		enemies.clear();
	}
	
}
