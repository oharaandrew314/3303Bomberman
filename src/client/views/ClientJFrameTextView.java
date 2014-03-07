package client.views;

import javax.swing.JMenuBar;

import common.models.Player;
import common.views.JFrameTextView;

public class ClientJFrameTextView extends JFrameTextView {
	
	private int playerId;

	public ClientJFrameTextView() {
		super();
	}

	public ClientJFrameTextView(JMenuBar menuBar) {
		super(menuBar);
	}

	@Override
	public String displayPlayerDead(Player player) {
		return "You have died!";
	}

	@Override
	public String displayConnectionAccepted(int playerId) {
		this.playerId = playerId;
		return "You have been added to the game as 'Player " + playerId + "'";
	}

	@Override
	public String displayConnectionRejected() {
		return "You have been rejected from the game";
	}

	@Override
	public String displayStartGame() {
		return "The game has started!";
	}

	@Override
	public String displayEndGame(Player player) {
		if (player.id == playerId){
			return "You have won!";
		}
		return player.name + " has won the game.";
	}

}
