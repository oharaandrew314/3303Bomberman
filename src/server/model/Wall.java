package server.model;

public class Wall extends Box {

	public Wall() {
		super("Wall");
	}

	@Override
	public boolean isDestructible() {
		return true;
	}

}
