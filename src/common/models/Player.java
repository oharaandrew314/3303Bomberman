package common.models;

public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	private final int START_NUM_BOMBS = 1;
	public final int playerId;
	
	private int numBombs; // no bombs in milestone 1
	
	public Player(int playerId){
		super("Player " + playerId);
		numBombs = START_NUM_BOMBS;
		this.playerId = playerId;
	}
	
	public void drobBomb(){
		throw new UnsupportedOperationException(); // no bombs in milestone 1
	}

	@Override
	public boolean isHideable() {
		return false;
	}
}
