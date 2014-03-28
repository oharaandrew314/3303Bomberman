package client.controllers;

import java.net.InetSocketAddress;

import server.controllers.SimulationListener;
import server.controllers.SimulationTimer;
import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.EndGameEvent;

public abstract class Client extends GameController implements SimulationListener {

	protected int playerId;
	private SimulationTimer timer;
	
	public Client() {
		this(NetworkController.LOCALHOST);
	}
	
	public Client(String serverAddress){
        this(serverAddress, NetworkController.SERVER_PORT);
	}
	
	public Client(String serverAddress, int serverPort){
		this(new InetSocketAddress(serverAddress, serverPort));
	}
	
	public Client(InetSocketAddress address){
		nwc.startListeningOnAnyAvailablePort();
		nwc.addPeer(address);
		send(new ConnectEvent(isSpectator()));
		playerId = -1; // no Id until given by server
		
		timer = new SimulationTimer();
		timer.addListener(this);
		timer.start();
	}
	
	@Override
	public void simulationUpdate(long now){
		nwc.requestAllEvents();
	}
	
	@Override
	public void onTimerReset() {}

	@Override
	public Event receive(Event event) {
		if (event instanceof PlayerDeadEvent){
			processPlayerDead((PlayerDeadEvent) event);
		}
		else if (event instanceof ConnectAcceptedEvent){
			processConnectionAccepted((ConnectAcceptedEvent) event);
		}
		else if (event instanceof ConnectRejectedEvent){
			processConnectionRejected();
		}
		else if (event instanceof EndGameEvent){
			endGame((EndGameEvent) event);
		}
		else if (event instanceof GameStartEvent){
			setGameStarted();
		}
		else if (event instanceof GameKeyEventAck){
			keyEventAcknowledged((GameKeyEventAck) event);
		}
		updateView(event);
		return null;
	}
	
	@Override
	protected void updateView(Event event){
		event.setPlayerID(playerId); //little hack, substitute local playerId
		super.updateView(event);
	}
	
	public int getPlayerId(){
		return playerId;
	}
	
	protected void setGameStarted(){
		setState(GameState.gameRunning);
	}
	
	protected void endGame(EndGameEvent winEvent){
		setState(GameState.idle);
	}
	
	protected void processPlayerDead(PlayerDeadEvent event){
		setState(GameState.idle);
	}
	
	public boolean isAcceptingConnections(){
		return false;
	}
	
	@Override
	public void stop(){
		if (getState() != GameState.stopped){
			setState(GameState.stopping);
			send(new DisconnectEvent());
		} else {
			super.stop();
		}
	}
	
	protected void processConnectionAccepted(ConnectAcceptedEvent event) {
		setState(GameState.idle);
		playerId = event.getAssignedPlayerId();
	}
	
	protected void processConnectionRejected() {
		setState(GameState.stopped);
		stop();
	}
	
	// Abstract methods
	protected abstract boolean isSpectator();
	
	// Optional methods (currently no functionality)
	protected void keyEventAcknowledged(GameKeyEvent event){}
}
