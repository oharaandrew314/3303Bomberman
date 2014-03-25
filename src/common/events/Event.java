package common.events;

import java.io.Serializable;

public abstract class Event implements Serializable {
	private static final long serialVersionUID = 6289295918217598226L;
	private int peerID;
    
    public void setPeerID(int peerID) {
        this.peerID = peerID;
    }
    
    public int getPeerID() {
        return peerID;
    }
    
    @Override
    public String toString(){
    	return getClass().getSimpleName();
    }
}
