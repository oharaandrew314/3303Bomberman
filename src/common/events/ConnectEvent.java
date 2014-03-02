package common.events;

public class ConnectEvent extends Event {
	private static final long serialVersionUID = -4136297588390368743L;
	
	public final boolean spectator;
	
	public ConnectEvent(boolean spectate){
		spectator = spectate;
	}
}
