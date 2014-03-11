package common.models;

import java.util.concurrent.Semaphore;

public class BombFactory {
	
	public static final int INIT_MAX_BOMBS = 1, INIT_FUSE_TIME = 2000;
	private int numBombs;
	private final Semaphore bombSem;

	public BombFactory() {
		numBombs = INIT_MAX_BOMBS;
		bombSem = new Semaphore(INIT_MAX_BOMBS);
	}
	
	public Bomb createBomb(){
		bombSem.acquireUninterruptibly();
		numBombs--;
		return new Bomb(this, INIT_FUSE_TIME);
	}
	
	public void bombDetonated(Bomb bomb){
		if (bomb == null){
			throw new NullPointerException("Detonated bomb was null!");
		}
		bombSem.release();
		numBombs++;
	}
	
	public void increaseMaxBombs(){
		numBombs++;
		bombSem.release();
	}
	
	public int getNumBombs(){
		return numBombs;
	}

}
