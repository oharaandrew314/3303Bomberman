package common.models.powerups;

import common.models.Entity;

public class Powerup extends Entity{

	private static final long serialVersionUID = -9016034152135153878L;

	public Powerup(String name) {
		super(name);
	}

	@Override
	public boolean isPassable() {
		return true;
	}
	
	@Override
	public boolean isDestructible(){
		return false;
	}
	
	@Override
	public String toString(){
		return "P";
	}
}
