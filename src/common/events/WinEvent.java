package common.events;

import common.models.Grid;
import common.models.Player;

public class WinEvent extends Event {

	private static final long serialVersionUID = 4972263064235877315L;
	public final Player player;
	public final Grid grid;
        
	public WinEvent(Player player, Grid finalGridState) {
		this.player = player;
		grid = finalGridState;
	}

}
