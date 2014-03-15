package common.models;

import java.io.Serializable;
import java.nio.BufferUnderflowException;

public class BombFactory implements Serializable {
	
	private static final long serialVersionUID = -5483632574557742377L;
	public static final int INIT_MAX_BOMBS = 1;
	private int numBombs;
	private int blastRange;

	public BombFactory() {
		numBombs = INIT_MAX_BOMBS;
		blastRange = Bomb.INIT_RANGE;
	}
	
	public synchronized Bomb createBomb(){
		if (numBombs == 0){
			throw new BufferUnderflowException();
		}
		numBombs--;
		return new Bomb(this, blastRange);
	}
	
	public synchronized void bombDetonated(Bomb bomb){
		if (bomb == null){
			throw new NullPointerException("Detonated bomb was null!");
		}
		numBombs++;
	}
	
	public synchronized void increaseMaxBombs(){
		numBombs++;
	}
	
	public synchronized void increaseBlastRange(){
		blastRange++;
	}
	
	public synchronized int getNumBombs(){
		return numBombs;
	}
	
	public synchronized int getBlastRange(){
		return blastRange;
	}

}
