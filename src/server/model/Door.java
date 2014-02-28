package server.model;

public class Door extends Entity {

	public Door(String name) {
		super(name);
	}

	@Override
	public boolean isPassable() {
		return true;
	}
}
