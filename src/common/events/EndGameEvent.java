package common.events;

import common.models.Grid;
import common.models.units.Player;

public class EndGameEvent extends Event {

	private static final long serialVersionUID = 4972263064235877315L;
	public final Player player;
	public final Grid grid;
	
	public EndGameEvent(Grid finalGridState){
		this(null, finalGridState);
	}
        
	public EndGameEvent(Player player, Grid finalGridState) {
		this.player = player;
		grid = finalGridState;
	}

}
