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
	
	private static enum State {stopped, idle, gameRunning, stopping };
	private State state = State.stopped;

	public Client() {
		this(NetworkController.LOCALHOST);
	}
	
	public Client(String serverAddress){
        nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		nwc.send(new ConnectEvent(isSpectator()));
	}

	@Override
	public Event receive(Event event) {
		if (event instanceof PlayerDeadEvent){
			processPlayerDead((PlayerDeadEvent) event);
		}
		else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted();
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
	
	protected void setGameStarted(){
		state = State.gameRunning;
	}
	
	protected void endGame(WinEvent winEvent){
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
	
	@Override
	public synchronized void stop(){
		if (state != State.stopped){
			state = State.stopping;
			send(new DisconnectEvent());
		} else {
			super.stop();
		}
	}
	
	protected  void processConnectionAccepted() {
		state = State.idle;
	}
	
	protected synchronized void processConnectionRejected() {
		state = State.stopped;
		stop();
	}
	
	// Abstract methods
	protected abstract boolean isSpectator();
	
	// Optional methods (currently no functionality)
	protected void keyEventAcknowledged(GameKeyEvent event){}
}
