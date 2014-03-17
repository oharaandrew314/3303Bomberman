package common.models.powerups;


public class MysteryPowerup extends Powerup{

	private static final long serialVersionUID = -3223123672481107147L;
	private long invulnerabilityDuration;
	
	public MysteryPowerup(long duration){
		super("Mystery");
		invulnerabilityDuration = duration;
	}
	
	public long getDuration(){
		return invulnerabilityDuration;
	}
	
}
