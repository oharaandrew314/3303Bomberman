/**
 * 
 */
package common.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import common.models.units.Unit;


public class Square implements Serializable {

	private static final long serialVersionUID = 4935477501755008585L;
	private Entity impassableEntity;
	private List<Entity> passableEntities;
	private static final String EMPTY_SQUARE_SYMBOL = " "; 
	
	public Square(){
		impassableEntity = null;
		passableEntities = new ArrayList<Entity>();
	}
	
	public Square(Square square) {
		if (square.impassableEntity instanceof Bomb)
			this.impassableEntity = new Bomb((Bomb)square.impassableEntity);
		else if (square.impassableEntity instanceof Pillar)
			this.impassableEntity = new Pillar((Pillar)square.impassableEntity);
		else if (square.impassableEntity instanceof Wall)
			this.impassableEntity = new Wall((Wall)square.impassableEntity);
		this.passableEntities = new ArrayList<Entity>(square.passableEntities);
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
		
		else if (!isPassable() && !entity.isPassable()){
			throw new IllegalArgumentException(
				"Cannot place impassable entity on top of an impassable square"
			);
		}
		
		else if (!isPassable() && entity instanceof Unit){
			throw new IllegalArgumentException(
				"Cannot place unit on impassable square"
			);
		}
		
		else if (!entity.isPassable()){
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
		return getEntities().contains(entity);
	}
	
	public Entity findType(Class<? extends Entity> type){
		for (Entity entity : getEntities()){
			if (type.isInstance(entity)){
				return entity;
			}
		}
		return null;
	}
	
	public Entity getVisibleEntity(){
		// Impassable object gets priority
		if (!isPassable()) return impassableEntity;
		
		// Otherwise, pick a passable object if one exists
		Entity entity = findType(Unit.class); // check for unit first
		if (entity == null){
			for (Entity e : passableEntities){
				if (e.isVisible()){
					entity = e;
				}
			}
		}
		return entity;
	}
	
	@Override
	public synchronized String toString(){
		Entity visibleEntity = getVisibleEntity();
		return visibleEntity == null ? EMPTY_SQUARE_SYMBOL : visibleEntity.toString();
	}
}
