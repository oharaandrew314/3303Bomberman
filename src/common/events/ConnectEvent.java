package common.events;

import java.awt.Point;

public class ConnectEvent extends Event {
	private static final long serialVersionUID = -4136297588390368743L;
	
	public final boolean spectator;
	public final Point startLocation;
	
	public ConnectEvent(boolean spectate){
		spectator = spectate;
		startLocation = null;
	}
	
	/**
	 * ConnectEvent for testRunner -> spectator is false, since its the testRunner
	 * @param startLocation
	 */
	public ConnectEvent(Point startLocation){
		this.startLocation = startLocation;
		spectator = false;
	}
}
