package common.models;

public abstract class Box extends Entity {
	
	private static final long serialVersionUID = 932604203004443850L;

	public Box(String name){
		super(name);
	}
	
	@Override
	public boolean isPassable() {
		return false;
	}
	
	@Override
	public abstract String toString();
}
