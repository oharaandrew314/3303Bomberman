package server.controllers;

import common.controllers.GameController;
import common.events.Event;

public class Server extends GameController {

	public Server() {
		new SimulationTimer(this);
	}
	
	public void simulationUpdate(){
		//TODO: Implement
	}
	
	public static void main(String[] args){
		new Server();
	}

    @Override
    public void receive(Event event) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
