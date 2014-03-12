package common.models;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.*;

public class Grid implements Serializable {

	private static final long serialVersionUID = -2951160259913748859L;
	private final Dimension size;
	private final Square[][] squares;
	
	public Grid(Dimension size){
		// Note that due to row-major order, we have width as last coordinate
		// This is transparent to users of this class.
		squares = new Square[size.height][size.width];
		this.size = size;
		
		// initialize squares
		for (Point point : keySet()){
			squares[point.y][point.x]= new Square(); 
		}
	}

	public List<Entity> get(Point point){
		return getSquare(point).getEntities();
	}
	
	private Square getSquare(Point point){
		return squares[point.y][point.x];
	}
	
	public boolean set(Entity entity, Point point){
		 // Try to remove existing entity
		if (contains(entity)){
			remove(entity);
		}
		
		return squares[point.y][point.x].add(entity);
	}
	
	public Dimension getSize(){
		return new Dimension(size);
	}
	
	public Point find(Entity entity){
		Point point = search(entity);
		if (point == null){
			throw new IllegalArgumentException(entity.name + " not found in grid");
		}
		return point;
	}
	
	public boolean contains(Entity entity){
		return search(entity) != null;
	}
	
	private Point search(Entity entity){
		for (Point point : keySet()){
			if (get(point).contains(entity)){
				return point;
			}
		}
		return null;
	}
	
	public Set<Point> keySet(){
		Set<Point> points = new HashSet<Point>();
		
		for (int j = 0; j < size.height; j++){
			for (int i = 0; i < size.width; i++){
				points.add(new Point(i, j));
			}
		}
		
		return points;
	}
	
	public boolean isPassable(Point point){
		return getSquare(point).isPassable();
	}
	
	public boolean hasPlayer(Point point){
		for (Entity entity: get(point)){
			if (entity instanceof Player){
				return true;
			}
		}
		return false;
	}
	
	public boolean hasBombAt(Point point){
		for (Entity e : get(point)){
			if (e instanceof Bomb){
				return true;
			}
		}
		return false;
	}
	
	
	public Set<Point> getPossibleMoves(Point point){
		return getPossibleMoves(point, false);
	}
	
	public Set<Point> getPossibleMoves(Point point, boolean includeImpassable){
		Set<Point> adjacents = new HashSet<Point>();
		adjacents.add(new Point(point.x - 1, point.y));
		adjacents.add(new Point(point.x + 1, point.y));
		adjacents.add(new Point(point.x, point.y - 1));
		adjacents.add(new Point(point.x, point.y + 1));
		
		Set<Point> points = new HashSet<>();
		for (Point p : adjacents){
			if (new Rectangle(0, 0, size.width, size.height).contains(p)){
				if (includeImpassable || isPassable(p)){
					points.add(p);
				}
			}
		}
		
		return points;
	}
	
	public Set<Point> getAffectedExplosionSquares(Bomb bomb){
		Point loc = find(bomb);
		int range = bomb.getRange();
		Set<Point> affectedSquares = new HashSet<>();
		affectedSquares.add(loc);
		addNeighbors(affectedSquares, loc, range, 1);
		return affectedSquares;
	}
	
	protected void addNeighbors(Set<Point> points, Point loc, int range, int depth){		
		for(Point p : getPossibleMoves(loc, true)){
			if (!hasTypeAt(Pillar.class, p) && !points.contains(p)){
				points.add(p);
				if (depth < range){
					addNeighbors(points, p, range, depth + 1);
				}
			}
		}
	}
	
	public boolean hasTypeAt(Class<? extends Entity> type, Point location){
		for (Entity e : get(location)){
			if (type.isInstance(e)){
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		final int EXTRA_WIDTH = 2; //compensates for the '|' taking up two extra rows
		String lineSep = System.getProperty("line.separator");
		
		for (int i = 0; i < size.width + EXTRA_WIDTH; i++){
			sb.append("-");
		}
		sb.append(lineSep);
		
		for (int j = 0; j < size.height; j++){
			sb.append("|");
			for (int i = 0; i < size.width; i++){
				sb.append(squares[j][i].toString());
			}
			sb.append("|" + lineSep);
		}
		
		for (int i = 0; i < size.width + EXTRA_WIDTH; i++){
			sb.append("-");
		}
		return sb.toString();
	}
	
	public boolean remove(Entity entity){
		Point loc = find(entity);
		return getSquare(loc).remove(entity);
	} 
}
