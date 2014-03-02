package common.controllers;

import common.events.Event;
import common.models.Grid;

public abstract class GameController {
	
	protected final NetworkController nwc;
	protected Grid grid;

	public GameController() {
		nwc = new NetworkController(this);
	}

    public abstract void receive(Event event);
}
