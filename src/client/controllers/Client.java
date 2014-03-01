package client.controllers;

import common.controllers.GameController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;

public abstract class Client extends GameController {

	public Client() {
		nwc.startListeningOnDefaultClientPort();
	}
	
	public Client(int port){
		nwc.startListeningOn(port);
	}

	@Override
	public void receive(Event event) {
		if (event instanceof ViewUpdateEvent){
			processViewUpdate((ViewUpdateEvent) event);
		} else if (event instanceof PlayerDeadEvent){
			processPlayerDead();
		} else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted();
		} else if (event instanceof ConnectRejectedEvent){
			processConnectionRejected();
		}

	}
	
	protected abstract void processViewUpdate(ViewUpdateEvent event);
	protected abstract void processPlayerDead();
	protected abstract void processConnectionAccepted();
	protected abstract void processConnectionRejected();

}
