package server.controllers;

import java.util.Timer;
import java.util.TimerTask;


public class SimulationTimer extends TimerTask {
	
	public static final int
		MS_IN_S = 1000, 
		UPDATE_FREQ = 10,
		UPDATE_DELAY = MS_IN_S / UPDATE_FREQ;
	
	private final Server server;
	private final Timer timer;

	public SimulationTimer(Server server) {
		this.server = server;
		timer = new Timer();
		timer.scheduleAtFixedRate(this, 0, UPDATE_DELAY);
	}
	@Override
	public void run() {
		server.simulationUpdate();
	}
	
	public void stop(){
		timer.cancel();
	}
}
