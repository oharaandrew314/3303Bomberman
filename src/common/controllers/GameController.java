package common.controllers;

import java.util.Observable;

import common.events.Event;
import common.models.Grid;
import common.views.AbstractView;

public abstract class GameController extends Observable{
	
	public static enum GameState {
		stopped, idle, newGame, gameRunning, stopping, error
	};
	
	protected final NetworkController nwc;
	private AbstractView view;
	protected Grid grid;
	protected GameState state = GameState.stopped;

	public GameController() {
		nwc = new NetworkController(this);
	}
	
	public final Grid getGrid(){
		return grid;
	}
	
	public final GameState getState(){
		return state;
	}
	
	public final boolean isGameRunning(){
		return state == GameState.gameRunning;
	}
	
	public void setView(AbstractView view){
		this.view = view;
	}
	
	protected void send(Event event){
		nwc.send(event);
	}
	
	protected void updateView(Event event){
		if (view != null){
			view.handleEvent(state, event);
		}
	}
	
	public void stop(){
		nwc.stopListening();
		nwc.clear();
		if (view != null){
			view.close();
		}
	}
	
	public abstract Event receive(Event event);
	public abstract boolean isAcceptingConnections();
    
}
