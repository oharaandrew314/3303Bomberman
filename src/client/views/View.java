package client.views;

import common.models.Grid;
import common.models.Player;

public interface View {
	void updateView(Grid grid);
	void displayPlayerDead(Player player);
	void displayConnectionAccepted();
	void displayConnectionRejected();
	void displayStartGame();
	void displayEndGame(Grid grid, Player player);
	void close();
}
