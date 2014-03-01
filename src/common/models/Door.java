package common.models;

public class Door extends Entity {

	public Door() {
		super("Door");
	}

	@Override
	public boolean isPassable() {
		return true;
	}

	@Override
	public boolean isHideable() {
		return true;
	}
}
