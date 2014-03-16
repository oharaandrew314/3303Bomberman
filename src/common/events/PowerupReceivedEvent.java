package common.events;

import common.models.powerups.Powerup;
import common.models.units.Player;

public class PowerupReceivedEvent extends Event {
	
	private static final long serialVersionUID = 6755646817909841727L;
	public final Player player;
	public final Powerup powerup;

	public PowerupReceivedEvent(Player player, Powerup powerup) {
		this.player = player;
		this.powerup = powerup;
	}

}
