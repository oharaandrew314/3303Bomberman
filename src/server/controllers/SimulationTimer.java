package server.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class SimulationTimer {
	
	public static final double DEFAULT_TIME_MULTIPLIER = 1.0;
	public static final int
		MS_IN_S = 1000, 
		UPDATE_FREQ = 10,
		UPDATE_DELAY = MS_IN_S / UPDATE_FREQ;
	
	private final List<SimulationListener> listeners;
	private Timer timer;
	private static double TIME_MULTIPLIER = DEFAULT_TIME_MULTIPLIER;

	public SimulationTimer(){
		listeners = new ArrayList<>();
	}
	
	public void addListener(SimulationListener l){
		synchronized(listeners){
			listeners.add(l);
		}
	}
	
	public void start(){
		timer = new Timer();
		timer.scheduleAtFixedRate(
			new TimerTask() {
				@Override
				public void run() {
					long now = currentTimeMillis();
					for (SimulationListener l : getListeners()){
						l.simulationUpdate(now);
					}
				}
			},
			0,
			UPDATE_DELAY
		);
	}
	
	public void stop(){
		if (timer != null){
			timer.cancel();
		}
		for (SimulationListener l : getListeners()){
			l.onTimerReset();
		}
	}
	
	/**
	 * Make copy of listeners to give up lock quickly
	 * @return copy of current SimulationListeners
	 */
	private List<SimulationListener> getListeners(){
		List<SimulationListener> copy;
		synchronized(listeners){
			copy = new ArrayList<>(listeners);
		}
		return copy;
		
	}
	
	public static void setTimeMultiplier(double multiplier){
		if (multiplier > 0){
			TIME_MULTIPLIER = multiplier;
		} else {
			System.err.printf("Invlid time multiplier of: %g", multiplier);
		}
	}
	
	public static long currentTimeMillis(){
		return (long) (System.currentTimeMillis() * TIME_MULTIPLIER);
	}
	
}
