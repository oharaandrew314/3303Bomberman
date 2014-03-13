package client.views;

import common.models.Player;
import common.views.TextGenerator;

public class SpectatorTextGenerator implements TextGenerator{
	
	@Override
	public String getTitle() {
		return "Spectator";
	}

	@Override
	public String getPlayerDead(Player player) {
		return String.format("%s has died", player.name);
	}

	@Override
	public String getConnectionAccepted(int playerId) {
		return String.format("Player %d has been added to the game.", playerId);
	}

	@Override
	public String getConnectionRejected() {
		return "A player has been rejected/disconnected from the game.";
	}

	@Override
	public String getEndGame(Player player) {
		return String.format("The game is over.  %s has won!", player);
	}

	@Override
	public String getStartGame() {
		return "The game has begun!";
	}

	@Override
	public String getPlayerDisconnected(int playerId) {
		return String.format("Player %d has disconnected.", playerId);
	}
}
