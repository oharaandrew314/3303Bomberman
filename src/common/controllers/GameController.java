package common.controllers;

import java.util.Observable;

import common.events.Event;
import common.models.Grid;
import common.views.AbstractView;

public abstract class GameController extends Observable{
	
	protected final NetworkController nwc;
	private AbstractView view;
	protected Grid grid;

	public GameController() {
		nwc = new NetworkController(this);
	}
	
	public void setView(AbstractView view){
		this.view = view;
	}
	
	protected void send(Event event){
		nwc.send(event);
	}
	
	protected void updateView(Event event){
		if (view != null){
			view.handleEvent(event);
		}
	}

	public abstract boolean isGameRunning();
	public abstract Event receive(Event event);
	public abstract boolean isAcceptingConnections();
    public abstract void stop();
}
