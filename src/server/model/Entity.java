package server.model;

public abstract class Entity {
	protected int id;
	protected String name;
	
	public Entity(int id, String name){
		this.id = id;
		this.name = name;
	}
	abstract public boolean isPassable();
}
