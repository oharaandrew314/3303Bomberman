package common.models;

import java.util.ArrayList;
import java.util.List;

public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	public static final long INVULNERABLE_TIME = 1000*10;
	private final int START_NUM_BOMBS = 1;
	protected final BombFactory factory;
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
		this.playerId = playerId;
		
		powerups = new ArrayList<Powerup>(); //just to keep track of powerups (not really needed)
		addedBombs = 0;
		addedBombRange = 0;
		invulnerableTill = 0;
		immuneToBombs = false;
		factory = new BombFactory();
	}
	
	public Bomb getNextBomb(){
		return factory.createBomb();
	}
	
	@Override
	public String toString(){
		return String.valueOf(playerId);
	}
	
	/**
	 * Add the powerups to the players list of powerups and change the players properties accordingly
	 * @param powerup - powerup picked up by the player
	 */
	public void addPowerup(Powerup powerup){
		powerups.add(powerup);
		if(powerup instanceof BombPlusOnePowerup){
			addedBombs++;
			increaseMaxBombs();
		} else if(powerup instanceof BombRangePowerup){
			addedBombRange++;
			increaseBombRange();
		} else if(powerup instanceof MysteryPowerup){
			invulnerableTill = System.currentTimeMillis() + INVULNERABLE_TIME;
		} else if(powerup instanceof FlamePassPowerup){
			immuneToBombs = true;
		}
	}
	
	/**
	 * @return the number of BombRange powerups picked up
	 */
	public int getAddedBombRange(){
		return addedBombRange;
	}
	
	/**
	 * @return the number of BombPlusOnePowerups picked up
	 */
	public int getAddedBombs(){
		return addedBombs;
	}
	
	/**
	 * @return true if currentTime is less than the time when Invulnerability ends
	 */
	public boolean isInvulnerable(){
		return System.currentTimeMillis() < invulnerableTill;
	}
	
	/**
	 * @return true if player picked up a flamePass powerup, false otherwise
	 */
	public boolean isImmuneToBombs(){
		return immuneToBombs;
	}
	
	public int getNumBombs(){
		return factory.getNumBombs();
	}
	
	public int getBombRange(){
		return factory.getBlastRange();
	}
	
	public boolean hasBombs(){
		return getNumBombs() > 0;
	}
	
	public void increaseMaxBombs(){
		factory.increaseMaxBombs();
	}
	
	public void increaseBombRange(){
		factory.increaseBlastRange();
	}
}
