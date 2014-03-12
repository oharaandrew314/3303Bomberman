package common.views;

import common.models.Player;

public interface TextGenerator {

	public String getPlayerDead(Player player);
	public String getConnectionAccepted(int playerId);
	public String getConnectionRejected();
	public String getEndGame(Player player);
	public String getStartGame();
	public String getPlayerDisconnected(int playerId);
}
