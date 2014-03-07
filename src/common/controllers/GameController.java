package common.controllers;

import java.util.Observable;

import common.events.Event;
import common.models.Grid;
import common.views.View;

public abstract class GameController extends Observable{
	
	protected final NetworkController nwc;
	private final View view;
	protected Grid grid;
	
	public GameController(){
		this(null);
	}

	public GameController(View view) {
		nwc = new NetworkController(this);
		this.view = view;
	}
	
	protected void send(Event event){
		nwc.send(event);
	}
	
	protected void updateView(Event event){
		view.handleEvent(event);
	}

	public abstract boolean isGameRunning();
	public abstract Event receive(Event event);
	public abstract boolean isAcceptingConnections();
    public abstract void stop();
}
