package common.models;

public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	protected final BombFactory factory;
	public final int playerId;
	
	@SuppressWarnings("unused")
	private int numBombs; // no bombs in milestone 1
	
	private long invulnerableTill;
	private long immuneToBombsTill;
	private int addedBombs;
	private int addedBombRange;
	
	public Player(int playerId){
		super("Player " + playerId);
		this.playerId = playerId;
		
		addedBombs = 0;
		addedBombRange = 0;
		invulnerableTill = 0;
		immuneToBombsTill = 0;
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
		if(powerup instanceof BombPlusOnePowerup){
			addedBombs++;
			increaseMaxBombs();
		} else if(powerup instanceof BombRangePowerup){
			addedBombRange++;
			increaseBombRange();
		} else if(powerup instanceof MysteryPowerup){
			invulnerableTill = System.currentTimeMillis() + ((MysteryPowerup) powerup).getDuration();
		} else if(powerup instanceof FlamePassPowerup){
			immuneToBombsTill = System.currentTimeMillis() + ((FlamePassPowerup) powerup).getDuration();
		}
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
		return System.currentTimeMillis() < immuneToBombsTill;
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
	
	private void increaseBombRange(){
		factory.increaseBlastRange();
	}
}
