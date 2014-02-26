package server.model;

public class Door extends Entity {

	public Door(int id, String name) {
		super(id, name);
	}

	@Override
	public boolean isPassable() {
		return true;
	}

	@Override
	public String toString(){
		return "D";
	}
}
