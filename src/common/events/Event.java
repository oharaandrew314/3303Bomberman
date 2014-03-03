package common.events;

import java.io.Serializable;

public abstract class Event implements Serializable {
	private static final long serialVersionUID = 6289295918217598226L;
	private int playerID = -1;
    
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
    
    public int getPlayerID() {
        return playerID;
    }
}
