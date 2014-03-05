package server.controllers;

import java.util.Timer;
import java.util.TimerTask;


public class SimulationTimer {
	
	public static final int
		MS_IN_S = 1000, 
		UPDATE_FREQ = 10,
		UPDATE_DELAY = MS_IN_S / UPDATE_FREQ;
	
	private final Server server;
	private Timer timer;

	public SimulationTimer(Server server) {
		this.server = server;
	}
	
	public synchronized void start(){
		stop();
		timer = new Timer();
		timer.scheduleAtFixedRate(
			new TimerTask() {
				@Override
				public void run() { server.simulationUpdate(); }
			},
			0,
			UPDATE_DELAY
		);
	}
	
	public synchronized void stop(){
		if (timer != null){
			timer.cancel();
		}
	}
}
