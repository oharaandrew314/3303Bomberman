package test.integration.helpers;

/**
 * Wait/Notify Synchronization helper.
 * Essentially a mutable boolean that can be passed-by-reference
 * @author Andrew O'Hara
 */
public class Condition {
	
	public boolean cond = false;
	private final Object lock;
	
	public Condition(Object lock){
		this.lock = lock;
	}
	
	public void waitCond(){
		synchronized(lock){
			while(!cond){
				try {
					lock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void notifyCond(){
		synchronized(lock){
			cond = true;
			lock.notify();
		}
	}
}
