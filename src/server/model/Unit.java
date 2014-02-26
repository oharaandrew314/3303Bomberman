package server.model;

public abstract class Unit extends Entity {
	public Unit(int id, String name) {
		super(id, name);
	}

	@Override
	public boolean isPassable(){
		// true since we give players a chance to "pass through" another unit...
		// they just die when they do.
		return true;
	}
}
