package server.model;

public abstract class Box extends Entity {
	
	public Box(String name){
		super(name);
	}
	
	@Override
	public boolean isPassable() {
		return false;
	}
	
	@Override
	public String toString(){
		return isDestructible() ? "." : "X";
	}

	@Override
	public boolean isHideable() {
		return false;
	}
	
	public abstract boolean isDestructible();

}
