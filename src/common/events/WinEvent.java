package common.events;

import common.models.Player;

public class WinEvent extends Event {

	private static final long serialVersionUID = 4972263064235877315L;

	public WinEvent(Player p) {
		this.setPlayerID(p.playerId);
	}

}
