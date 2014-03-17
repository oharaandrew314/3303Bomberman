package common.models;

public class Door extends Entity {

	private static final long serialVersionUID = 1275068658290607107L;

	public Door() {
		super("Door");
	}
	
	public Door(Door door) {
		super(door);
	}

	@Override
	public boolean isPassable() {
		return true;
	}
	
	@Override
	public boolean isDestructible(){
		return false;
	}
}
