package test.integration.helpers;

/**
 * Wait/Notify Synchronization helper.
 * Essentially a mutable boolean that can be passed-by-reference
 * @author Andrew O'Hara
 */
public class Condition {
	
	public boolean cond = false;
	
	public synchronized void waitCond(){
		while(!cond){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		cond = false;
	}
	
	public synchronized void notifyCond(){
		cond = true;
		notify();
	}
}