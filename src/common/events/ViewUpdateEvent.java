package common.events;

import server.model.Grid;


public class ViewUpdateEvent extends Event {
    private Grid grid;
    
    public ViewUpdateEvent(Grid grid) {
        this.grid = grid;
    }
    
    public Grid getGrid() {
        return grid;
    }
}
