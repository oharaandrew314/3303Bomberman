package common.models;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 * This enemy moves in a line until running into something.
 */
public class LineEnemy extends Enemy {

	private static Set<Point> validDirections;
	private static final long serialVersionUID = 7951827748781091635L;
	private Point direction;
	
	public LineEnemy() {
		this(new Point(0, 0));
	}
	public LineEnemy(Point startingDirection){
		super("LineEnemy");
		validDirections = new HashSet<Point>();
		validDirections.add(new Point(0, 0));
		validDirections.add(new Point(0, 1));
		validDirections.add(new Point(0, -1));
		validDirections.add(new Point(1, 0));
		validDirections.add(new Point(-1, 0));
		
		setDirection(startingDirection);
	}
	
	public Point getDirection(){
		return new Point(direction);
	}
	public void setDirection(Point direction){
		if (!validDirections.contains(direction))
			throw new IllegalArgumentException("Invalid direction");
		this.direction = new Point(direction);
	}
}
