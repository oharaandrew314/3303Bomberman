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
	private Entity impassableEntity;
	private List<Entity> passableEntities;
	private static final String EMPTY_SQUARE_SYMBOL = " "; 
	
	public Square(){
		impassableEntity = null;
		passableEntities = new ArrayList<Entity>();
	}
	
	/**
	 * Checks if this square is passable. A square is passable
	 * if none of it's entities are impassable.
	 * @return true if the square is passable, false otherwise.
	 */
	public boolean isPassable(){
		return impassableEntity == null;
	}
	
	public boolean add(Entity entity){
		if (entity == null){
			throw new IllegalArgumentException("Cannot add null entity");
		}
		
		if (!entity.isPassable()){
			if (!isPassable()) return false; //tried to add second impassable entity
			impassableEntity = entity;
			return true;
		}
		return passableEntities.add(entity);
	}
	
	public boolean remove(Entity entity){
		if (entity == null){
			throw new IllegalArgumentException("Cannot remove null entity");
		}
		
		if (entity == impassableEntity){
			impassableEntity = null;
			return true;
		}
		return passableEntities.remove(entity);
	}
	
	public boolean contains(Entity entity){
		return entity == impassableEntity || passableEntities.contains(entity);
	}
	
	@Override
	public String toString(){
		// Impassable object gets priority
		if (!isPassable()) return impassableEntity.toString();
		
		// Otherwise, pick a passable object if one exists
		return passableEntities.isEmpty() ? EMPTY_SQUARE_SYMBOL : passableEntities.iterator().next().toString();
	}
}
