package common.models;

public class Pillar extends Box {

	private static final long serialVersionUID = 2909832054013703313L;

	public Pillar() {
		super("Pillar");
	}

	@Override
	public boolean isDestructible() {
		return false;
	}

}
