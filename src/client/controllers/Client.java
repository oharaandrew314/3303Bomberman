package client.controllers;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Grid;

public abstract class Client extends GameController {
	
	private boolean running = false;

	public Client() {
		this(NetworkController.LOCALHOST);
	}
	
	public Client(String serverAddress){
                nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		nwc.send(new ConnectEvent(isSpectator()));
	}

	@Override
	public void receive(Event event) {
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
			startGame();
		}
	}
	
	protected abstract boolean isSpectator();
	protected abstract void processViewUpdate(Grid grid);
	protected abstract void processPlayerDead(PlayerDeadEvent event);
	protected abstract void processConnectionAccepted();
	protected abstract void processConnectionRejected();
	
	protected void startGame(){
		running = true;
	}
	
	protected void endGame(WinEvent winEvent){
		processViewUpdate(winEvent.grid);
		running = false;
	}
	
	@Override
	public boolean isGameRunning(){
		return running;
	}

}
