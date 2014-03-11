package server.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SimulationTimer {
	
	public static final int
		MS_IN_S = 1000, 
		UPDATE_FREQ = 10,
		UPDATE_DELAY = MS_IN_S / UPDATE_FREQ;
	
	private final List<SimulationListener> listeners;
	private Timer timer;

	public SimulationTimer(){
		listeners = new ArrayList<>();
	}
	
	public synchronized void addListener(SimulationListener l){
		listeners.add(l);
	}
	
	public synchronized void start(){
		timer = new Timer();
		timer.scheduleAtFixedRate(
			new TimerTask() {
				@Override
				public void run() {
					for (SimulationListener l : listeners){
						l.simulationUpdate();
					}
				}
			},
			0,
			UPDATE_DELAY
		);
	}
	
	public synchronized void stop(){
		if (timer != null){
			timer.cancel();
		}
		for (SimulationListener l : listeners){
			l.onRemovedFromTimer();
		}
		listeners.clear();
	}
}
