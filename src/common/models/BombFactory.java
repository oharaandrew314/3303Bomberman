package common.models;

import java.io.Serializable;
import java.util.concurrent.Semaphore;

public class BombFactory implements Serializable {
	
	private static final long serialVersionUID = -5483632574557742377L;
	public static final int INIT_MAX_BOMBS = 1;
	private final Semaphore bombSem;

	public BombFactory() {
		bombSem = new Semaphore(INIT_MAX_BOMBS);
	}
	
	public Bomb createBomb(){
		bombSem.acquireUninterruptibly();
		return new Bomb(this);
	}
	
	public void bombDetonated(Bomb bomb){
		if (bomb == null){
			throw new NullPointerException("Detonated bomb was null!");
		}
		bombSem.release();
	}
	
	public void increaseMaxBombs(){
		bombSem.release();
	}
	
	public int getNumBombs(){
		return bombSem.availablePermits();
	}

}
