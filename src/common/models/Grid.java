package common.models;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.Stack;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

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
	
	public Grid(Grid grid) {
		this.size = new Dimension(grid.size);
		this.squares = new Square[size.height][size.width];
		for (int y = 0; y < size.height; y++){
			for (int x = 0; x < size.width; x++){
				this.squares[x][y] = new Square(grid.squares[x][y]);
			}
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
	
	public Set<Point> getPossibleMoves(Point point){
		return getPointsInRadius(point, 1, false);
	}
	
	public Set<Point> getAffectedExplosionSquares(Bomb bomb){
		return getPointsInRadius(find(bomb), bomb.getRange(), true);
	}

	protected Set<Point> getPointsInRadius(
		Point origin, int radius, boolean includeImpassable
	){
		Set<Point> points = new HashSet<>();
		points.add(origin);
		points.addAll(getStraightPath(origin, new Point(-1, 0), radius, includeImpassable));
		points.addAll(getStraightPath(origin, new Point(1, 0), radius, includeImpassable));
		points.addAll(getStraightPath(origin, new Point(0, -1), radius, includeImpassable));
		points.addAll(getStraightPath(origin, new Point(0, 1), radius, includeImpassable));
		return points;
	}
	
	private Set<Point> getStraightPath(
		Point origin, Point delta, int length, boolean includeImpassable
	){
		Set<Point> points = new HashSet<>();
		Rectangle bounds = new Rectangle(size);
		
		Point current = new Point(origin);
		for (int i=0; i<length; i++){
			current.translate(delta.x, delta.y);
			
			if (bounds.contains(current)){
				// Add to path if this is an explodable square
				if (
					(includeImpassable || isPassable(current))
					&& !hasTypeAt(Pillar.class, current)
				){
					points.add(new Point(current));
				}
				
				// If an impassable object was encountered; end
				if (!isPassable(current)){
					break;
				}
			}			
		}
		return points;
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
	
	/**
	 * Gets the shortest path from origin to destination
	 * @param origin The starting point of the path
	 * @param destination The destination point
	 * @return A list containing the shortest path from origin to destination, 
	 *  or null if none exists. Includes origin and destination.
	 */
	public List<Point> getShortestPath(Point origin, Point destination){
		// This method is an implementation of Dijkstra's algorithm
		
		Set<Point> keySet = keySet();
		if (!keySet.contains(origin) || !keySet.contains(destination)){
			throw new IllegalArgumentException("Origin and/or destination not in grid");
		}
		
		// this maps a point to its previous point in a shortest path to the origin
		Map<Point, Point> previous = new HashMap<Point, Point>();
		previous.put(origin, null);
		
		// these are the points added most recently to the previous map (the outer edge)
		Set<Point> newPoints = new HashSet<Point>();
		newPoints.add(origin);
		
		// continue dijkstra's algorithm while destination not found, and points still exist to be found
		while (!previous.containsKey(destination) && !newPoints.isEmpty()){
			
			Set<Point> nextPoints = new HashSet<Point>();
			//Deviation from dijkstra's algorithm - search through all new points added
			//since it is known they are an equal distance away from the origin anyway
			//(each adjacency is a distance of one unit)
			for (Point p : newPoints){
				Set<Point> possibleMoves = getPossibleMoves(p);
				possibleMoves.remove(p);
				for (Point q : possibleMoves){
					if (!previous.containsKey(q)){
						nextPoints.add(q);
						previous.put(q, p);
					}
				}
			}
			
			newPoints = nextPoints;
		}
		
		if (!previous.containsKey(destination)) return null; //no path possible
		
		// retrace path backwards
		Stack<Point> backpath = new Stack<Point>();
		Point current = destination;
		while (current != null){
			backpath.push(current);
			current = previous.get(current);
		}
		
		// reverse backpath into proper forward path
		List<Point> path = new ArrayList<Point>();
		while (!backpath.isEmpty()){
			path.add(backpath.pop());
		}
		
		return path;
	}
}
