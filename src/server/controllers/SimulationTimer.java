package server.controllers;

import java.util.Timer;
import java.util.TimerTask;


public class SimulationTimer extends TimerTask {
	
	public static final int
		MS_IN_S = 1000, 
		UPDATE_FREQ = 60,
		UPDATE_DELAY = MS_IN_S / UPDATE_FREQ;
	
	private final Server server;

	public SimulationTimer(Server server) {
		this.server = server;
		new Timer().scheduleAtFixedRate(this, 0, UPDATE_DELAY);
	}
	@Override
	public void run() {
		server.simulationUpdate();		
	}
}
