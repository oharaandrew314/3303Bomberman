package server.model;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

public class Grid {

	private final Dimension size;
	private Square[][] squares;
	
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
		for (int i = 0; i < squares.length; i++){
			for (int j = 0; j < squares[i].length; j++){
				if (square == squares[i][j]) return new Point(i, j);
			}
		}
		
		return null; //throw exception?
	}
	
	
	public Set<Square> getPossibleMoves(Point point){
		Point[] adjacents = new Point[4];
		adjacents[0] = new Point(point.x - 1, point.y);
		adjacents[1] = new Point(point.x + 1, point.y);
		adjacents[2] = new Point(point.x, point.y - 1);
		adjacents[3] = new Point(point.x, point.y + 1);
		
		Set<Square> retval = new HashSet<Square>();
		for (Point p : adjacents){
			if (p.x >= 0 && p.y >= 0 && p.x < size.width && p.y < size.height){
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
		for (int i = 0; i < size.width + 3; i++){
			sb.append("-");
		}
		sb.append("\n");
		
		for (int i = 0; i < squares.length; i++){
			sb.append("|");
			for (int j = 0; j < squares[i].length; j++){
				sb.append(squares[i][j].toString());
			}
			sb.append("|\n");
		}
		
		for (int i = 0; i < size.width + 3; i++){
			sb.append("-");
		}
		return sb.toString();
	}
	
	// Temp? main for testing...might not be temp since this would be
	// part of the client
	public static void main(String[] args) {
		Grid g = new Grid(new Dimension (2, 3));
		
		Square[] squares = new Square[6];
		for (int i = 0; i < squares.length; i++){
			squares[i] = new Square();
		}
		
		squares[0].add(new Player(0, "Fred"));
		squares[4].add(new Door(6, "Exit of Freedom"));
		
		for (int i = 0; i < g.getSize().width; i++){
			for (int j = 0; j < g.getSize().height; j++){
				g.set(squares[i * 3 + j], new Point(i, j));
			}
		}
		
		System.out.println(g);
	}

}
