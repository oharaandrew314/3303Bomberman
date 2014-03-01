package common.events;

import java.io.Serializable;

public abstract class Event implements Serializable {
    private int playerID;
    
    public void setPlayerID(int playerID) {
        this.playerID = playerID;
    }
    
    public int getPlayerID() {
        return playerID;
    }
}
