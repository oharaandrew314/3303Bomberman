package test.helpers;

import static org.junit.Assert.assertTrue;

/**
 * Wait/Notify Synchronization helper.
 * Essentially a mutable boolean that can be passed-by-reference
 * @author Andrew O'Hara
 */
public class Condition {
	
	public static final int DEFAULT_TIMEOUT = 1000;
	private boolean waiting;
	private final int timeout;
	
	public Condition(){
		this(DEFAULT_TIMEOUT);
	}
	
	public Condition(int timeout){
		this.timeout = timeout;
	}
	
	public synchronized void waitCond(){
		waiting = true;
		try {
			wait(timeout);
			assertTrue("Condition timed out", !waiting);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiting = false;
	}
	
	public synchronized void notifyCond(){
		waiting = false;
		notify();
	}
}