package client.controllers;

import client.views.TextView;
import client.views.View;
import common.events.ViewUpdateEvent;

public class Spectator extends Client {

	private View view;
	
	public Spectator(View view){
		super();
		this.view = view;
	}
	public Spectator(int port, View view){
		super(port);
		this.view = view;
	}
	
	@Override
	protected void processViewUpdate(ViewUpdateEvent event) {
		view.updateView(event);
	}

	@Override
	protected void processPlayerDead() {
		// Spectator doesn't own a player, doesn't care
	}

	@Override
	protected void processConnectionAccepted() {
		System.out.println("Connection Accepted");
	}

	@Override
	protected void processConnectionRejected() {
		System.out.println("Connection Rejected");
	}
	
	public static void main(String[] args){
		new Spectator(new TextView());
	}

}
