package common.models;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.*;

public class Grid implements Serializable {

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
	
	public Square get(Point point){
		return squares[point.y][point.x];
	}
	
	public boolean set(Entity entity, Point point){
		return squares[point.y][point.x].add(entity);
	}
	
	public Dimension getSize(){
		return new Dimension(size);
	}
	
	public Point find(Entity entity){
		for (Point point : keySet()){
			if (get(point).contains(entity)){
				return point;
			}
		}
		throw new IllegalArgumentException("Square not found in grid");
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
	
	
	public Set<Square> getPossibleMoves(Point point){
		Set<Point> adjacents = new HashSet<Point>();
		adjacents.add(new Point(point.x - 1, point.y));
		adjacents.add(new Point(point.x + 1, point.y));
		adjacents.add(new Point(point.x, point.y - 1));
		adjacents.add(new Point(point.x, point.y + 1));
		
		Set<Square> retval = new HashSet<Square>();
		for (Point p : adjacents){
			if (new Rectangle(0, 0, size.width, size.height).contains(p)){
				Square candidate = this.get(p);
				if (candidate.isPassable()) { // can't move into impassible squares
					retval.add(candidate);
				}
			}
		}
		
		return retval;
	}
	
	public Set<Square> getAffectedExplosionSquares(){
		throw new UnsupportedOperationException(); //no bombs in milestone 1
	}

	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer();
		final int EXTRA_WIDTH = 2; //compensates for the '|' taking up two extra rows
		
		for (int i = 0; i < size.width + EXTRA_WIDTH; i++){
			sb.append("-");
		}
		sb.append("\n");
		
		for (int j = 0; j < size.height; j++){
			sb.append("|");
			for (int i = 0; i < size.width; i++){
				sb.append(squares[j][i].toString());
			}
			sb.append("|\n");
		}
		
		for (int i = 0; i < size.width + EXTRA_WIDTH; i++){
			sb.append("-");
		}
		return sb.toString();
	}
}
