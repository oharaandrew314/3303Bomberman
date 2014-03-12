package server.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import common.models.Bomb;

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
				server.detonateBomb(bomb);
			}
			if (bomb.isDetonated()){
				bombs.remove(bomb);
			}
		}
	}

	@Override
	public void onTimerReset() {
		bombs.clear();
	}
}
