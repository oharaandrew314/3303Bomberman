package common.models;

public class Enemy extends Unit {

	private static final long serialVersionUID = 7031350047918428917L;
	private MovementPattern movementPattern;
	
	public Enemy(String name) {
		super(name);
	}
	public Enemy(String name, MovementPattern pattern){
		super(name);
		this.movementPattern = pattern;
	}

	public MovementPattern getMovementPattern(){
		return movementPattern;
	}
}
