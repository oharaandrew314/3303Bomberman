package client.views;

import common.controllers.GameController.GameState;
import common.models.powerups.Powerup;
import common.models.units.Player;
import common.views.TextGenerator;

public class SpectatorTextGenerator implements TextGenerator{
	
	@Override
	public String getTitle(GameState state) {
		return String.format("Bomberman (Spectator) - %s", state);
	}

	@Override
	public String getPlayerDead(Player player) {
		return String.format("%s has died", player.name);
	}

	@Override
	public String getConnectionAccepted(int playerId) {
		if (playerId == -1) return "A spectator has connected";
		return String.format("Player %d has been added to the game", playerId);
	}

	@Override
	public String getConnectionRejected() {
		return "A player has been rejected/disconnected from the game.";
	}

	@Override
	public String getEndGame(Player player) {
		return String.format(
			"The game is over.  %s has won!",
			player == null ? "No one" : player.name
		);
	}

	@Override
	public String getStartGame() {
		return "The game has begun!";
	}

	@Override
	public String getPlayerDisconnected(int playerId) {
		if (playerId == -1) return "A spectator has disconnected";
		return String.format("Player %d has disconnected.", playerId);
	}

	@Override
	public String getPowerupMessage(Player player, Powerup powerup) {
		return String.format(
			"%s has received the %s powerup.", player, powerup.name
		);
	}
}
