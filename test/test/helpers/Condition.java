package test.helpers;

/**
 * Wait/Notify Synchronization helper.
 * Essentially a mutable boolean that can be passed-by-reference
 * @author Andrew O'Hara
 */
public class Condition {
	
	private boolean waiting;
	
	public synchronized void waitCond(){
		waiting = true;
		while(waiting){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		waiting = false;
	}
	
	public synchronized void notifyCond(){
		waiting = false;
		notify();
	}
}