package client.controllers;

public class Spectator extends Client {
	
	@Override
	protected boolean isSpectator() {
		return true;
	}
}
