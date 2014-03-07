package common.controllers;

import java.util.Observable;

import client.views.View;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Grid;

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
		if (view != null){
			if (event instanceof ViewUpdateEvent){
				ViewUpdateEvent viewEvent = (ViewUpdateEvent) event;
				view.updateView(viewEvent.getGrid());
			}
			else if (event instanceof PlayerDeadEvent){
				PlayerDeadEvent deadEvent = (PlayerDeadEvent) event;
				view.displayPlayerDead(deadEvent.player);
			}
			else if (event instanceof ConnectAcceptedEvent){
				view.displayConnectionAccepted();
			}
			else if (event instanceof ConnectRejectedEvent){
				view.displayConnectionRejected();
			}
			else if (event instanceof WinEvent){
				WinEvent winEvent = (WinEvent) event;
				view.displayEndGame(winEvent.grid, winEvent.player);
				view.updateView(winEvent.grid);
			}
			else if (event instanceof GameStartEvent){
				view.displayStartGame();
			}
		}
	}

	public abstract boolean isGameRunning();
	public abstract Event receive(Event event);
	public abstract boolean isAcceptingConnections();
    public abstract void stop();
}
