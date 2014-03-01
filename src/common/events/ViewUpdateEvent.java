package common.events;

import common.models.Grid;


public class ViewUpdateEvent extends Event {
    private Grid grid;
    
    public ViewUpdateEvent(Grid grid) {
        this.grid = grid;
    }
    
    public Grid getGrid() {
        return grid;
    }
}
