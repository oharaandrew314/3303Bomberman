package client.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import common.events.GameKeyEvent;

public class PlayableClient extends Client implements KeyListener {
	
	public PlayableClient(){
		super();
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
		send(new GameKeyEvent(keyCode));
	}
	
	public void startGame(){
		pressKey(KeyEvent.VK_ENTER);
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		pressKey(e);
	}
}
