package server.model;

import java.io.Serializable;

public abstract class Entity implements Serializable {
	private static int NEXT_ID = 1;
	
	final public int id;
	final public String name;
	
	public Entity(String name){
		this.id = NEXT_ID;
		NEXT_ID++;
		
		this.name = name;
	}
	abstract public boolean isPassable();
	
	@Override
	public String toString(){
		return name.substring(0, 1);
	}
}
