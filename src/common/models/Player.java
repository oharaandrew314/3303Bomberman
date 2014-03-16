package common.models;

import server.controllers.SimulationTimer;

public class Player extends Unit {

	private static final long serialVersionUID = 7322528472259511719L;
	protected final BombFactory factory;
	public final int playerId;
	
	private long invulnerableTill;
	private long immuneToBombsTill;
	
	public Player(int playerId){
		super("Player " + playerId);
		this.playerId = playerId;

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
			increaseMaxBombs();
		} else if(powerup instanceof BombRangePowerup){
			factory.increaseBlastRange();
		} else if(powerup instanceof MysteryPowerup){
			invulnerableTill = SimulationTimer.currentTimeMillis() + ((MysteryPowerup) powerup).getDuration();
		} else if(powerup instanceof FlamePassPowerup){
			immuneToBombsTill = SimulationTimer.currentTimeMillis() + ((FlamePassPowerup) powerup).getDuration();
		}
	}
	
	/**
	 * @return true if currentTime is less than the time when Invulnerability ends
	 */
	@Override
	public boolean isInvulnerable(){
		return SimulationTimer.currentTimeMillis() < invulnerableTill;
	}
	
	/**
	 * @return true if player picked up a flamePass powerup, false otherwise
	 */
	@Override
	public boolean isImmuneToBombs(){
		return (SimulationTimer.currentTimeMillis() < immuneToBombsTill || isInvulnerable());
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
}
