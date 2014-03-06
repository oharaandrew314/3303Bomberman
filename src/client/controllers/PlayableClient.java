package client.controllers;

import java.awt.event.KeyEvent;

import common.controllers.NetworkController;
import common.events.GameKeyEvent;

public abstract class PlayableClient extends Client {
	
	public PlayableClient(){
		this(NetworkController.LOCALHOST);
	}
	
	public PlayableClient(String serverAddress){
        super(serverAddress);
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	// Helpers
	
	public void pressKey(KeyEvent keyEvent){
		pressKey(keyEvent.getKeyCode());
	}
	
	public void pressKey(int keyCode){
		nwc.send(new GameKeyEvent(keyCode));
	}
	
	public void startGame(){
		pressKey(KeyEvent.VK_ENTER);
	}
	
	// Overrides

	@Override
	protected void processConnectionAccepted() {
	}

	@Override
	protected void processConnectionRejected() {
	}

}
