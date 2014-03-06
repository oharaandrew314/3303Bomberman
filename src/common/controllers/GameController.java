package common.controllers;

import java.util.Observable;

import common.events.Event;
import common.models.Grid;

public abstract class GameController extends Observable{
	
	protected final NetworkController nwc;
	protected Grid grid;

	public GameController() {
		nwc = new NetworkController(this);
	}
	
	protected void send(Event event){
		nwc.send(event);
	}

	public abstract boolean isGameRunning();
	public abstract boolean isAcceptingConnections();
    public abstract Event receive(Event event);
    public abstract void stop();
}
