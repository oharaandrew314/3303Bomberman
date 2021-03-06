package common.views;

import common.controllers.GameController.GameState;
import common.models.powerups.Powerup;
import common.models.units.Player;

public interface TextGenerator {

	public String getTitle(String connectionString, GameState state);
	public String getPlayerDead(Player player);
	public String getConnectionAccepted(int playerId);
	public String getConnectionRejected();
	public String getEndGame(Player player);
	public String getStartGame();
	public String getPlayerDisconnected(int playerId);
	public String getPowerupMessage(Player player, Powerup powerup);
}
