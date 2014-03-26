package client.controllers;

import java.net.InetSocketAddress;

public class Spectator extends Client {
	
	public Spectator(InetSocketAddress address){
		super(address);
	}
	
	@Override
	protected boolean isSpectator() {
		return true;
	}
}
