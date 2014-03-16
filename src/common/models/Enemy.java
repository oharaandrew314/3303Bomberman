package common.models;

import server.controllers.AIScheduler;

public class Enemy extends Unit {

	private static final long serialVersionUID = 7031350047918428917L;
	
	private long lastMovement;
	
	public Enemy(String name) {
		super(name);
	}
	
	public boolean isTimeToMove(long now){
		if (now - lastMovement > AIScheduler.ENEMY_MOVE_FREQ){
			lastMovement = now;
			return true;
		}
		return false;
	}
}
