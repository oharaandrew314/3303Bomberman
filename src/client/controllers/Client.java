package client.controllers;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;

public abstract class Client extends GameController {

	public Client() {
		this(
			NetworkController.LOCALHOST,
			NetworkController.DEFAULT_CLIENT_PORT
		);
	}
	
	public Client(String serverAddress){
		this(serverAddress, NetworkController.DEFAULT_CLIENT_PORT);
	}
	
	public Client(int port){
		this(NetworkController.LOCALHOST, port);
	}
	
	public Client(String serverAddress, int clientPort){
		nwc.addPeer(serverAddress, NetworkController.SERVER_PORT);
		nwc.startListeningOn(clientPort);
		nwc.send(new ConnectEvent());
	}

	@Override
	public void receive(Event event) {
		if (event instanceof ViewUpdateEvent){
			processViewUpdate((ViewUpdateEvent) event);
		} else if (event instanceof PlayerDeadEvent){
			processPlayerDead((PlayerDeadEvent) event);
		} else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted();
		} else if (event instanceof ConnectRejectedEvent){
			processConnectionRejected();
		} else if (event instanceof WinEvent){
			processWinEvent((WinEvent) event);
		}
	}
	
	protected abstract void processViewUpdate(ViewUpdateEvent event);
	protected abstract void processPlayerDead(PlayerDeadEvent event);
	protected abstract void processConnectionAccepted();
	protected abstract void processConnectionRejected();
	protected abstract void processWinEvent(WinEvent event);

}
