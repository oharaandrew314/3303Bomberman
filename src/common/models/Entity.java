package common.models;

import java.io.Serializable;

public abstract class Entity implements Serializable {

	private static final long serialVersionUID = 5839465145181345621L;
	private static int NEXT_ID = 1;
	
	final public int id;
	public final String name;
	private boolean visible = true;
	
	public Entity(String name){
		this.id = NEXT_ID;
		NEXT_ID++;
		
		this.name = name;
	}
	abstract public boolean isPassable();
	
	/**
	 * @return Whether an imPassable object can be placed on top of this entity
	 */
	public abstract boolean isHideable();
	
	@Override
	public String toString(){
		return name.substring(0, 1);
	}
	
	@Override
	public boolean equals(Object o){
		if (o instanceof Entity){
			return ((Entity)o).id == id;
		}
		return false;
	}
	
	public boolean isVisible(){
		return visible;
	}
	
	public void setVisible(boolean visible){
		this.visible = visible;
	}
	
	public boolean isDestructible(){
		return true;
	}
}
