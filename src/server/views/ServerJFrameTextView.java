package server.views;

import javax.swing.JFrame;

import common.models.Player;
import common.views.JFrameTextView;

public class ServerJFrameTextView extends JFrameTextView {

	public ServerJFrameTextView() {
		super();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	@Override
	public String displayPlayerDead(Player player) {
		return player.name + " has died";
	}

	@Override
	public String displayConnectionAccepted(int playerId) {
		return "Player " + playerId + " has been added to the game.";
	}

	@Override
	public String displayConnectionRejected() {
		return "A connection attempt was rejected";
	}

	@Override
	public String displayStartGame() {
		return "The game has started!";
	}

	@Override
	public String displayEndGame(Player player) {
		return "The game has ended.  " + player.name + " has won!";
	}

}
