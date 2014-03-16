package client.controllers;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.WinEvent;

public abstract class Client extends GameController {

	protected int playerId;
	
	public Client() {
		this(NetworkController.LOCALHOST);
		playerId = -1; // no Id until given by server
	}
	
	public Client(String serverAddress){
        nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		send(new ConnectEvent(isSpectator()));
	}

	@Override
	public Event receive(Event event) {
		if (event instanceof PlayerDeadEvent){
			processPlayerDead((PlayerDeadEvent) event);
		}
		else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted((ConnectAcceptedEvent) event);
		}
		else if (event instanceof ConnectRejectedEvent){
			processConnectionRejected();
		}
		else if (event instanceof WinEvent){
			endGame((WinEvent) event);
		}
		else if (event instanceof GameStartEvent){
			setGameStarted();
		}
		else if (event instanceof GameKeyEventAck){
			keyEventAcknowledged((GameKeyEventAck) event);
		}
		updateView(event);
		return null;
	}
	
	@Override
	protected void updateView(Event event){
		if (view != null){
			event.setPlayerID(playerId); //little hack, substitute local playerId
			view.handleEvent(state, event);
		}
	}
	
	public int getPlayerId(){
		return playerId;
	}
	
	protected void setGameStarted(){
		state = GameState.gameRunning;
	}
	
	protected void endGame(WinEvent winEvent){
		state = GameState.idle;
	}
	
	protected void processPlayerDead(PlayerDeadEvent event){
		state = GameState.idle;
	}
	
	public boolean isAcceptingConnections(){
		return false;
	}
	
	@Override
	public void stop(){
		if (state != GameState.stopped){
			state = GameState.stopping;
			send(new DisconnectEvent());
		} else {
			super.stop();
		}
	}
	
	protected void processConnectionAccepted(ConnectAcceptedEvent event) {
		state = GameState.idle;
		playerId = event.getAssignedPlayerId();
	}
	
	protected void processConnectionRejected() {
		state = GameState.stopped;
		stop();
	}
	
	// Abstract methods
	protected abstract boolean isSpectator();
	
	// Optional methods (currently no functionality)
	protected void keyEventAcknowledged(GameKeyEvent event){}
}
