package common.models;

public class Bomb extends Entity {

	public static final int FUSE_TIME = 2000, INIT_RANGE = 1;
	private static final long serialVersionUID = 5546213627619819574L;
	private final BombFactory factory;
	private boolean detonated;

	public Bomb(BombFactory factory) {
		super("Bomb");
		this.factory = factory;
		detonated = false;
	}
	
	public void setDetonated(){
		if (detonated){
			throw new RuntimeException("Cannot detonate a bomb more than once.");
		}
		detonated = true;
		factory.bombDetonated(this);
	}
	
	public boolean isDetonated(){
		return detonated;
	}

	@Override
	public boolean isPassable() {
		return true;
	}

	@Override
	public boolean isHideable() {
		return true;
	}
	
	public int getRange(){
		return INIT_RANGE;
	}

}
