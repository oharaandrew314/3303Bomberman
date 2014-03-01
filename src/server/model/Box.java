package server.model;

public class Box extends Entity {
	
	public final boolean destructible;
	
	public Box(boolean destructible){
		super(null);
		this.destructible = destructible;
	}
	
	@Override
	public boolean isPassable() {
		return false;
	}
	
	@Override
	public String getName(){
		return destructible ? "Wall" : "Pillar";
	}
	
	@Override
	public String toString(){
		return destructible ? "." : "X";
	}

}
