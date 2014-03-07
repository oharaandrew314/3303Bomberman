package client.controllers;

import java.awt.Point;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.*;
import common.models.Grid;

public abstract class Client extends GameController {
	
	private static enum State {idle, gameRunning };
	private State state;

	public Client() {
		this(NetworkController.LOCALHOST);
		state = State.idle;
	}
	
	public Client(String serverAddress){
        nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		nwc.send(new ConnectEvent(isSpectator()));
	}
	
	public Client(String serverAddress, Point startLoc){
		nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		nwc.send(new ConnectEvent(startLoc));
	}

	@Override
	public Event receive(Event event) {		
		if (event instanceof ViewUpdateEvent){
			processViewUpdate(((ViewUpdateEvent)event).getGrid());
		} else if (event instanceof PlayerDeadEvent){
			processPlayerDead((PlayerDeadEvent) event);
		} else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted();
		} else if (event instanceof ConnectRejectedEvent){
			processConnectionRejected();
		} else if (event instanceof WinEvent){
			endGame((WinEvent) event);
		} else if (event instanceof GameStartEvent){
			setGameStarted();
		} else if (event instanceof GameKeyEventAck){
			keyEventAcknowledged((GameKeyEventAck) event);
		}
		return null;
	}
	
	protected abstract boolean isSpectator();
	protected abstract void processViewUpdate(Grid grid);
	protected abstract void processConnectionAccepted();
	protected abstract void processConnectionRejected();
	
	protected void keyEventAcknowledged(GameKeyEvent event){}
	
	protected void setGameStarted(){
		state = State.gameRunning;
	}
	
	protected void endGame(WinEvent winEvent){
		processViewUpdate(winEvent.grid);
		state = State.idle;
	}
	
	protected void processPlayerDead(PlayerDeadEvent event){
		state = State.idle;
	}
	
	@Override
	public boolean isGameRunning(){
		return state == State.gameRunning;
	}
	
	public boolean isAcceptingConnections(){
		return false;
	}
}
