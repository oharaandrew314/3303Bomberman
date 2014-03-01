package common.models;

public class Pillar extends Box {

	public Pillar() {
		super("Pillar");
	}

	@Override
	public boolean isDestructible() {
		return false;
	}

}
