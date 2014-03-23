package server.views;

import common.controllers.GameController.GameState;

import client.views.SpectatorTextGenerator;

public class ServerTextGenerator extends SpectatorTextGenerator {

	@Override
	public String getTitle(String connectionString, GameState state) {
		return String.format(
			"Bomberman Server: %s (%s)", connectionString, state
		);
	}

}
