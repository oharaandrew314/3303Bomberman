package common.models;

public class Enemy extends Unit {

	private static final long serialVersionUID = 7031350047918428917L;
	
	private final long movementPeriod = 1200;
	private long lastMovement;
	
	public Enemy(String name) {
		super(name);
	}
	
	public boolean isTimeToMove(long now){
		if (now - lastMovement > movementPeriod){
			lastMovement = now;
			return true;
		}
		return false;
	}
}
