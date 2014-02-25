
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

}
