package common.models;

public class Door extends Entity {

	private static final long serialVersionUID = 1275068658290607107L;

	public Door() {
		super("Door");
		setVisible(false);
	}

	@Override
	public boolean isPassable() {
		return true;
	}

	@Override
	public boolean isHideable() {
		return true;
	}
	
	@Override
	public boolean isVisible(){
		return false;
	}
}
