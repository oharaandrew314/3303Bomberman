package common.models;

public class Powerup extends Entity{

	private static final long serialVersionUID = -9016034152135153878L;

	public Powerup(String name) {
		super(name);
	}
	
	public Powerup(){
		super("P");
	}

	@Override
	public boolean isPassable() {
		return true;
	}

}
