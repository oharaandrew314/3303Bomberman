package client.controllers;

import java.awt.event.KeyEvent;

import client.views.View;

import common.events.GameKeyEvent;

public class PlayableClient extends Client {
	
	public PlayableClient(){
		super();
	}
	
	public PlayableClient(View view){
		super(view);
	}
	
	public PlayableClient(String serverAddress){
		super(serverAddress);
	}
	
	public PlayableClient(String serverAddress, View view){
        super(serverAddress, view);
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
}
