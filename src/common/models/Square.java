/**
 * 
 */
package common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Square implements Serializable {

	private static final long serialVersionUID = 4935477501755008585L;
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
	
	public synchronized boolean add(Entity entity){
		if (entity == null){
			throw new IllegalArgumentException("Cannot add null entity");
		}
		
		if (!isPassable() && !entity.isHideable()){
			throw new IllegalArgumentException(
				"Cannot place nonHidable object on impassable Square"
			);
		}
		
		if (!entity.isPassable()){
			if (!isPassable()){
				throw new IllegalArgumentException("Tried to add second impassable entity");
			}
			impassableEntity = entity;
			return true;
		}
		return passableEntities.add(entity);
	}
	
	public List<Entity> getEntities(){
		List<Entity> entities = new ArrayList<>(passableEntities);
		if (impassableEntity != null){
			entities.add(impassableEntity);
		}
		return entities;
	}
	
	public synchronized boolean remove(Entity entity){
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
	public synchronized String toString(){
		// Impassable object gets priority
		if (!isPassable()) return impassableEntity.toString();
		
		// Otherwise, pick a passable object if one exists
		for (Entity entity : passableEntities){
			if (entity.isVisible()){
				return entity.toString();
			}
		}
		
		return EMPTY_SQUARE_SYMBOL;
	}
}
