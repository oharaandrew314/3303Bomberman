package server.controllers;

public interface SimulationListener {
	
	public void simulationUpdate();
	public void onTimerReset();

}
