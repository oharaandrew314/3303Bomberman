/**
 * 
 */
package server.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class Square {
	private List<Entity> entities;
	private static final String EMPTY_SQUARE_SYMBOL = " "; 
	
	public Square(){
		entities = new ArrayList<Entity>();
	}
	
	/**
	 * Checks if this square is passable. A square is passable
	 * if none of it's entities are impassable.
	 * @return true if the square is passable, false otherwise.
	 */
	public boolean isPassable(){
		for (Entity entity : entities){
			if (!entity.isPassable()) return false;
		}
		
		return true;
	}
	
	public boolean add(Entity entity){
		return entities.add(entity);
	}
	public boolean remove(Entity entity){
		return entities.remove(entity);
	}
	
	@Override
	public String toString(){
		// Impassable object gets priority
		for (Entity entity : entities){
			if (!entity.isPassable()) return entity.toString();
		}
		
		// Otherwise, pick a passable object if one exists
		return entities.isEmpty() ? EMPTY_SQUARE_SYMBOL : entities.iterator().next().toString();
	}
}
