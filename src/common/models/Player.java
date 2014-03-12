package common.models;

import java.util.ArrayList;
import java.util.List;

public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	private final int START_NUM_BOMBS = 1;
	public final int playerId;
	
	@SuppressWarnings("unused")
	private int numBombs; // no bombs in milestone 1
	
	private List<Powerup> powerups;
	private long invulnerableTill;
	private int addedBombs;
	private int addedBombRange;
	private boolean immuneToBombs;
	
	public Player(int playerId){
		super("Player " + playerId);
		numBombs = START_NUM_BOMBS;
		this.playerId = playerId;
		
		powerups = new ArrayList<Powerup>(); //just to keep track of powerups (not really needed)
		addedBombs = 0;
		addedBombRange = 0;
		invulnerableTill = 0;
		immuneToBombs = false;
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
	
	public void addPowerup(Powerup powerup){
		powerups.add(powerup);
		if(powerup instanceof BombPlusOnePowerup){
			addedBombs++;
		} else if(powerup instanceof BombRangePowerup){
			addedBombRange++;
		} else if(powerup instanceof MysteryPowerup){
			invulnerableTill = System.currentTimeMillis() + (1000*15);//TODO:remove magic numbers
		} else if(powerup instanceof FlamePassPowerup){
			immuneToBombs = true;
		}
	}
	
	public int getAddedBombRange(){
		return addedBombRange;
	}
	
	public int getAddedBombs(){
		return addedBombs;
	}
	
	public boolean isInvulnerable(){
		long currentTime = System.currentTimeMillis();
		return currentTime < invulnerableTill;
	}
	
	public boolean isImmuneToBombs(){
		return immuneToBombs;
	}
	
	
}
