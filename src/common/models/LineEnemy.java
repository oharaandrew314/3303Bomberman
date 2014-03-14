package common.models;

public class LineEnemy extends Enemy {

	private static final long serialVersionUID = 7951827748781091635L;
	private Direction direction;
	
	public LineEnemy(String name) {
		this(name, Direction.UP);
	}
	public LineEnemy(String name, Direction startingDirection){
		super(name);
		setDirection(startingDirection);
	}
	
	public Direction getDirection(){
		return direction;
	}
	public void setDirection(Direction direction){
		this.direction = direction;
	}
}
