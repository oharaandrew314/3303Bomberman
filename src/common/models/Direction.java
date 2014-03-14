package common.models;

import java.awt.Point;

public enum Direction {
	UP,
	DOWN,
	LEFT,
	RIGHT;
	
	public static final Point getTranslationPoint(Direction d){
		switch (d){
		case UP: return new Point(0, -1);
		case DOWN: return new Point(0, 1);
		case LEFT: return new Point(-1, 0);
		case RIGHT: return new Point(0, 1);
		default: throw new IllegalArgumentException("Unknown direction");
		}
	}
}
