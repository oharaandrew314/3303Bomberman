package common.models.powerups;


public class MysteryPowerup extends Powerup{
	
	public static final int DEFAULT_DURATION = 10000;

	private static final long serialVersionUID = -3223123672481107147L;
	private long invulnerabilityDuration;
	
	public MysteryPowerup(){
		this(DEFAULT_DURATION);
	}
	
	public MysteryPowerup(long duration){
		super("Mystery");
		invulnerabilityDuration = duration;
	}
	
	public long getDuration(){
		return invulnerabilityDuration;
	}
	
}
