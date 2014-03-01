package common.controllers;

import common.events.Event;

public abstract class GameController {

	public GameController() {
		// TODO Auto-generated constructor stub
	}

        public abstract void receive(Event event);
}
