package common.models;

public class Bomb extends Entity {

	private static final long serialVersionUID = 5546213627619819574L;
	private final BombFactory factory;
	public final int fuseTime;

	public Bomb(BombFactory factory, int fuseTime) {
		super("Bomb");
		this.factory = factory;
		this.fuseTime = fuseTime;
	}
	
	public void detonate(){
		factory.bombDetonated(this);
	}

	@Override
	public boolean isPassable() {
		return false;
	}

	@Override
	public boolean isHideable() {
		return false;
	}

}
