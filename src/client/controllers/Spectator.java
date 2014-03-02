package client.controllers;

import client.views.TextView;
import client.views.View;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;

public class Spectator extends Client {

	private View view;
	
	public Spectator(View view){
		super();
		this.view = view;
	}
	public Spectator(String serverAddress, View view){
		super(serverAddress);
		this.view = view;
	}
	
	@Override
	protected void processViewUpdate(ViewUpdateEvent event) {
		view.updateView(event);
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
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
	@Override
	protected void processWinEvent(WinEvent event) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public boolean isGameRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}
