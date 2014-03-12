package common.views;

import java.awt.event.KeyListener;

import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.Event;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Grid;

public abstract class AbstractView {
	
	private final TextGenerator textGen;
	
	public AbstractView(TextGenerator textGen){
		this.textGen = textGen;
	}
	
	public void handleEvent(Event event){
		String message = null;
		
		if (event instanceof ViewUpdateEvent){
			ViewUpdateEvent viewEvent = (ViewUpdateEvent) event;
			displayGrid(viewEvent.getGrid());
		}
		else if (event instanceof PlayerDeadEvent){
			PlayerDeadEvent deadEvent = (PlayerDeadEvent) event;
			message = textGen.getPlayerDead(deadEvent.player);
		}
		else if (event instanceof ConnectAcceptedEvent){
			message = textGen.getConnectionAccepted(event.getPlayerID());
		}
		else if (event instanceof ConnectRejectedEvent){
			message = textGen.getConnectionRejected();
		}
		else if (event instanceof WinEvent){
			WinEvent winEvent = (WinEvent) event;
			message = textGen.getEndGame(winEvent.player);
			displayGrid(winEvent.grid);
		}
		else if (event instanceof GameStartEvent){
			message = textGen.getStartGame();
		}
		else if (event instanceof DisconnectEvent){
			message = textGen.getPlayerDisconnected(event.getPlayerID());
		}
		
		if (message != null){
			displayMessage(message);
		}
	}
	
	public abstract void displayMessage(String message);
	public abstract void displayGrid(Grid grid);
	public abstract void close();
	public abstract void addKeyListener(KeyListener l);
}
