package server.model;

public class Door extends Entity {

	public Door() {
		super("Door");
	}

	@Override
	public boolean isPassable() {
		return true;
	}
}
