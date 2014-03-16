package common.models.units;

public class Enemy extends Unit {

	private static final long serialVersionUID = 7031350047918428917L;
	public static final int ENEMY_MOVE_FREQ = 1200;
	
	private long lastMovement;
	
	public Enemy(String name) {
		super(name);
	}
	
	public boolean isTimeToMove(long now){
		if (now - lastMovement > ENEMY_MOVE_FREQ){
			lastMovement = now;
			return true;
		}
		return false;
	}
}
