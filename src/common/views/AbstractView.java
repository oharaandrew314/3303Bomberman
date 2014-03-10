package common.views;

import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.Event;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;

public abstract class AbstractView implements View {
	
	public void handleEvent(Event event){
		String message = null;
		
		if (event instanceof ViewUpdateEvent){
			ViewUpdateEvent viewEvent = (ViewUpdateEvent) event;
			message = updateView(viewEvent.getGrid());
		}
		else if (event instanceof PlayerDeadEvent){
			PlayerDeadEvent deadEvent = (PlayerDeadEvent) event;
			message = displayPlayerDead(deadEvent.player);
		}
		else if (event instanceof ConnectAcceptedEvent){
			message = displayConnectionAccepted(event.getPlayerID());
		}
		else if (event instanceof ConnectRejectedEvent){
			displayConnectionRejected();
		}
		else if (event instanceof WinEvent){
			WinEvent winEvent = (WinEvent) event;
			message = displayEndGame(winEvent.player);
			updateView(winEvent.grid);
		}
		else if (event instanceof GameStartEvent){
			message = displayStartGame();
		}
		else if (event instanceof DisconnectEvent){
			message = getPlayerDisconnected(event.getPlayerID());
		}
		
		if (message != null){
			displayMessage(message);
		}
	}
	
	public String getPlayerDisconnected(int playerId){
		return "Player " + playerId + " was disconnected from the game";
	}
	
	public abstract void displayMessage(String message);
}
