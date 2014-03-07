package common.models;

import java.awt.Point;

public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	private final int START_NUM_BOMBS = 1;
	public final int playerId;
	public final Point startLoc;
	
	@SuppressWarnings("unused")
	private int numBombs; // no bombs in milestone 1
	
	public Player(int playerId){
		this(playerId, null);
	}
	
	public Player(int playerId, Point startLoc){
		super("Player" + playerId);
		numBombs = START_NUM_BOMBS;
		this.playerId = playerId;
		this.startLoc = startLoc;
	}
	
	public void drobBomb(){
		throw new UnsupportedOperationException(); // no bombs in milestone 1
	}

	@Override
	public boolean isHideable() {
		return false;
	}
	
	@Override
	public String toString(){
		return String.valueOf(playerId);
	}
}
