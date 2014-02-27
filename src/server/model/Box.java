package server.model;

public class Box extends Entity {
	
	public final boolean destructible;
	
	public Box(String name, boolean destructible){
		super(name);
		this.destructible = destructible;
	}
	
	@Override
	public boolean isPassable() {
		return false;
	}
	
	@Override
	public String toString(){
		return destructible ? "X" : ".";
	}

}
