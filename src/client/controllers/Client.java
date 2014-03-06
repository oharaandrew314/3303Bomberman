package client.controllers;

import client.views.View;
import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.*;

public abstract class Client extends GameController {
	
	private static enum State {idle, gameRunning };
	private final View view;
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
		this.view = view;
		
        nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		nwc.send(new ConnectEvent(isSpectator()));
	}
	
	public View getView(){
		return view;
	}

	@Override
	public Event receive(Event event) {		
		if (event instanceof ViewUpdateEvent){
			ViewUpdateEvent viewEvent = (ViewUpdateEvent) event;
			if (view != null) { view.updateView(viewEvent.getGrid()); }
		}
		else if (event instanceof PlayerDeadEvent){
			PlayerDeadEvent deadEvent = (PlayerDeadEvent) event;
			processPlayerDead(deadEvent);
			if (view != null) { view.displayPlayerDead(deadEvent.player); }
		}
		else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted();
			if (view != null) { view.displayConnectionAccepted(); }
		}
		else if (event instanceof ConnectRejectedEvent){
			processConnectionRejected();
			if (view != null) {view.displayConnectionRejected(); }
		}
		else if (event instanceof WinEvent){
			WinEvent winEvent = (WinEvent) event;
			endGame(winEvent);
			if (view != null) {
				view.displayEndGame(winEvent.grid, winEvent.player);
				view.updateView(winEvent.grid);
			}
		}
		else if (event instanceof GameStartEvent){
			setGameStarted();
			if (view != null) { view.displayStartGame(); }
		}
		else if (event instanceof GameKeyEventAck){
			keyEventAcknowledged((GameKeyEventAck) event);
		}
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
