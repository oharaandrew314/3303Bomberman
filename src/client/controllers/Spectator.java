package client.controllers;

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
		// TODO Auto-generated method stub

	}

	@Override
	protected void processConnectionRejected() {
		// TODO Auto-generated method stub

	}

}
