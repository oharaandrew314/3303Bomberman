package server.controllers;

public interface SimulationListener {
	
	public void simulationUpdate(long currentTimeMs);
	public void onTimerReset();

}
