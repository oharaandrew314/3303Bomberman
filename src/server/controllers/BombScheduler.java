package server.controllers;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import common.models.Bomb;
import common.models.Entity;
import common.models.Grid;

public class BombScheduler implements SimulationListener {
	
	private final Map<Bomb, Long> bombs;
	private final Server server;
	
	public BombScheduler(Server server){
		bombs = new HashMap<>();
		this.server = server;
	}
	
	public synchronized void scheduleBomb(Bomb bomb){
		bombs.put(
			bomb,
			System.currentTimeMillis() + Bomb.FUSE_TIME
		);
	}
	
	@Override
	public synchronized void simulationUpdate(){
		// Detonate bombs ready for detonation
		long now = System.currentTimeMillis();
		for (Entry<Bomb, Long> entry : bombs.entrySet()){
			Bomb bomb = entry.getKey();
			long detonationTime = entry.getValue();
			if (!bomb.isDetonated() && now - detonationTime >= 0){
				detonate(bomb);
			}
			if (bomb.isDetonated()){
				bombs.remove(bomb);
			}
		}
	}
	
	private void detonate(Bomb bomb){
		System.err.println("detonate");
		bomb.detonate();
		Grid grid = server.getGrid();
		for (Point p : grid.getAffectedExplosionSquares(bomb)){
			for (Entity entity : grid.get(p)){
				if (entity.isDestructible()){
					System.err.println("boom " + entity);
					grid.remove(entity);
				}
			}
		}
	}

	@Override
	public void onTimerReset() {
		bombs.clear();
	}
}
