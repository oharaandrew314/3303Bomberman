package client.views;

import common.controllers.GameController.GameState;
import common.models.Player;
import common.views.TextGenerator;

public class ClientTextGenerator implements TextGenerator {

	private int playerId = -1;
	
	@Override
	public String getTitle(GameState state) {
		String title = String.format("Bomberman - %s", state);
		if (playerId != -1){
			title += ": Player " + playerId;
		}
		return title;
	}

	@Override
	public String getPlayerDead(Player player) {
		return String.format("%s has died", player.name);
	}

	@Override
	public String getConnectionAccepted(int playerId) {
		this.playerId = playerId;
		return String.format(
			"You have been added to the game as player %d", playerId
		);
	}

	@Override
	public String getConnectionRejected() {
		return "You have been rejected/disconnected from the game.";
	}

	@Override
	public String getEndGame(Player player) {
		if (player.playerId == playerId){
			return "You have won the game!";
		}
		return String.format("The game is over.  %s has won!", player);
	}

	@Override
	public String getStartGame() {
		return "The game has begun!  Bring death to your enemies!";
	}

	@Override
	public String getPlayerDisconnected(int playerId) {
		if (playerId == this.playerId){
			return "You have disconnected from the game.";
		}
		return String.format("Player %d has disconnected.", playerId);
	}
}
