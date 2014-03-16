package common.models;

public class Bomb extends Entity {

	public static final int FUSE_TIME = 2000, INIT_RANGE = 1;
	private static final long serialVersionUID = 5546213627619819574L;
	private final BombFactory factory;
	private boolean detonated;
	private int blastRange;

	public Bomb(BombFactory factory) {
		this(factory, INIT_RANGE);
	}
	
	public Bomb(BombFactory factory, int range){
		super("Bomb");
		this.factory = factory;
		detonated = false;
		blastRange = range;
	}
	
	public synchronized void setDetonated(){
		if (detonated){
			throw new RuntimeException("Cannot detonate a bomb more than once.");
		}
		detonated = true;
		factory.bombDetonated(this);
	}
	
	public synchronized boolean isDetonated(){
		return detonated;
	}

	@Override
	public boolean isPassable() {
		return true;
	}
	
	public int getRange(){
		return blastRange;
	}

}
