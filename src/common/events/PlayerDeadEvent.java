package common.events;

import common.models.units.Player;

public class PlayerDeadEvent extends Event {
	
	private static final long serialVersionUID = -3950085382776099065L;
	public final Player player;

	public PlayerDeadEvent(Player player) {
		this.player = player;
	}

}
