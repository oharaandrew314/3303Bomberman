package client.controllers;

import client.views.TextView;
import client.views.View;
import common.controllers.NetworkController;
import common.events.*;
import common.models.Grid;

public class Spectator extends Client {

	private View view;
	
	public Spectator(View view){
		this(NetworkController.LOCALHOST, view);
	}
	public Spectator(String serverAddress, View view){
		super(serverAddress);
		this.view = view;
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		view.displayPlayerDead(event.player);
	}

	@Override
	protected void processConnectionAccepted() {
		view.displayConnectionAccepted();
	}

	@Override
	protected void processConnectionRejected() {
		view.displayConnectionRejected();
	}
	
	public static void main(String[] args){
		String networkAddress = NetworkController.LOCALHOST;
		if (args.length > 0){
			networkAddress = args[0];
		}
		
		new Spectator(networkAddress, new TextView());
	}
	
	@Override
	protected boolean isSpectator() {
		return true;
	}
	@Override
	protected void processViewUpdate(Grid grid) {
		view.updateView(grid);
	}

}
