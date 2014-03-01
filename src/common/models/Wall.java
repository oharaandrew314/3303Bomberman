package common.models;

public class Wall extends Box {

	public Wall() {
		super("Wall");
	}

	@Override
	public boolean isDestructible() {
		return true;
	}

}
