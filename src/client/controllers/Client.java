package client.controllers;

import client.views.View;
import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.*;

public abstract class Client extends GameController {
	
	private static enum State {idle, gameRunning };
	private State state;

	public Client() {
		this(NetworkController.LOCALHOST);
		state = State.idle;
	}
	
	public Client(View view){
		this(NetworkController.LOCALHOST, view);
	}
	
	public Client(String serverAddress){
		this(serverAddress, null);
	}
	
	public Client(String serverAddress, View view){
		super(view);
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
	
	protected  void processConnectionAccepted() {}
	protected void processConnectionRejected() {}
	public void stop(){}
	
	protected abstract boolean isSpectator();
	
	protected void keyEventAcknowledged(GameKeyEvent event){}
}
