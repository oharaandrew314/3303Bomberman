package common.events;

import common.models.Player;

public class WinEvent extends Event {

	private static final long serialVersionUID = 4972263064235877315L;
        public final Player player;
        
	public WinEvent(Player player) {
		this.player = player;
	}

}
