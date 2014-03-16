package common.views;

import common.controllers.GameController.GameState;
import common.models.Player;
import common.models.powerups.Powerup;

public interface TextGenerator {

	public String getTitle(GameState state);
	public String getPlayerDead(Player player);
	public String getConnectionAccepted(int playerId);
	public String getConnectionRejected();
	public String getEndGame(Player player);
	public String getStartGame();
	public String getPlayerDisconnected(int playerId);
	public String getPowerupMessage(Player player, Powerup powerup);
}
