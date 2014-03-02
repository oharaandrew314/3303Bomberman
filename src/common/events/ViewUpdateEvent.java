package common.events;

import common.models.Grid;


public class ViewUpdateEvent extends Event {
	private static final long serialVersionUID = 3205454428606455663L;
	private Grid grid;
    
    public ViewUpdateEvent(Grid grid) {
        this.grid = grid;
    }
    
    public Grid getGrid() {
        return grid;
    }
}
