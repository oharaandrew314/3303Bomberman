package client.controllers;

import client.views.View;

public class Spectator extends Client {
	
	public Spectator(View view){
		super(view);
	}
	
	public Spectator(String serverAddress, View view){
		super(serverAddress, view);
	}
	
	@Override
	protected boolean isSpectator() {
		return true;
	}
}
