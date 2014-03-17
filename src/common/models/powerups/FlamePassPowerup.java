package common.models.powerups;


public class FlamePassPowerup extends Powerup{
	
	public static final int DEFAULT_DURATION = 10000;

	private static final long serialVersionUID = -32253965770618896L;
	private long duration;
	
	public FlamePassPowerup(){
		this(DEFAULT_DURATION);
	}
	
	public FlamePassPowerup(long duration){
		super("Flamepass");
		this.duration = duration;
	}
	
	public long getDuration(){
		return duration;
	}
}
