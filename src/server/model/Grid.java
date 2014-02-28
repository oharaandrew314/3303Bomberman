package server.model;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.HashSet;
import java.util.Set;

public class Grid {

	private final Dimension size;
	private final Square[][] squares;
	
	public Grid(Dimension size){
		squares = new Square[size.width][size.height];
		this.size = size;
	}
	
	public Square get(Point point){
		return squares[point.x][point.y];
	}
	public void set(Square square, Point point){
		squares[point.x][point.y] = square;
	}
	public Dimension getSize(){
		return new Dimension(size);
	}
	
	public Point find(Square square){
		for (int i = 0; i < size.width; i++){
			for (int j = 0; j < size.height; j++){
				if (square == squares[i][j]) return new Point(i, j);
			}
		}
		
		throw new IllegalArgumentException("Square not found in grid");
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
		
		for (int i = 0; i < size.height + EXTRA_WIDTH; i++){
			sb.append("-");
		}
		sb.append("\n");
		
		for (int i = 0; i < size.height; i++){
			sb.append("|");
			for (int j = 0; j < size.width; j++){
				sb.append(squares[j][i].toString());
			}
			sb.append("|\n");
		}
		
		for (int i = 0; i < size.height + EXTRA_WIDTH; i++){
			sb.append("-");
		}
		return sb.toString();
	}
}
