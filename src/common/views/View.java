package common.views;

import java.awt.event.KeyListener;

import common.events.Event;
import common.models.Grid;
import common.models.Player;

public interface View {
	String updateView(Grid grid);
	String displayPlayerDead(Player player);
	String displayConnectionAccepted(int playerId);
	String displayConnectionRejected();
	String displayStartGame();
	String displayEndGame(Player player);
	String getPlayerDisconnected(int playerId);
	void addKeyListener(KeyListener l);
	void close();
	void handleEvent(Event event);
}
