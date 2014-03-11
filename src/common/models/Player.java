package common.models;


public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	private final BombFactory factory;
	public final int playerId;
	
	@SuppressWarnings("unused")
	private int numBombs; // no bombs in milestone 1
	
	public Player(int playerId){
		super("Player " + playerId);
		this.playerId = playerId;
		factory = new BombFactory();
	}
	public Bomb getNextBomb(){
		return factory.createBomb();
	}

	@Override
	public boolean isHideable() {
		return false;
	}
	
	@Override
	public String toString(){
		return String.valueOf(playerId);
	}
	
	public int getNumBombs(){
		return factory.getNumBombs();
	}
	
	public boolean hasBombs(){
		return getNumBombs() > 0;
	}
}
