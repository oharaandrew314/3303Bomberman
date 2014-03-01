package common.models;

public abstract class Unit extends Entity {

	private static final long serialVersionUID = -891986997581475820L;

	public Unit(String name) {
		super(name);
	}

	@Override
	public boolean isPassable(){
		// true since we give players a chance to "pass through" another unit...
		// they just die when they do.
		return true;
	}
}
