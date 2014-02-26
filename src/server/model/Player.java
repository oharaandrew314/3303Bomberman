package server.model;

public class Player extends Unit {
	private final int START_NUM_BOMBS = 1;
	
	private int numBombs; // no bombs in milestone 1
	
	public Player(int id, String name){
		super(id, name);
		numBombs = START_NUM_BOMBS;
	}
	
	public void drobBomb(){
		throw new UnsupportedOperationException(); // no bombs in milestone 1
	}
	
	@Override
	public String toString(){
		return "P";
	}
}
