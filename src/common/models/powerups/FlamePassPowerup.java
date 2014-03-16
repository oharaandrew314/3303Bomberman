package common.models.powerups;


public class FlamePassPowerup extends Powerup{

	private static final long serialVersionUID = -32253965770618896L;
	private long duration;
	
	public FlamePassPowerup(long duration){
		super("Flamepass");
		this.duration = duration;
	}
	
	public long getDuration(){
		return duration;
	}
}
