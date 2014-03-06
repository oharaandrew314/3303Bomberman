package test.integration.helpers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import server.content.GridLoader;
import server.controllers.Server;

import common.models.Grid;
import common.models.Player;

public class MockServer extends Server {
	
	public void newGame(){
		newGame(GridLoader.loadGrid("test/testGrid2.json"));
	}
	
	public Grid getGrid(){
		return grid;
	}
	
	public List<Player> getPlayers(){
		return new ArrayList<Player>(players.values());
	}
	
	public Player movePlayerTo(int playerId, Point newPos){
		Player player = players.get(playerId);
		if (grid.contains(player)){
			grid.remove(player);
		}
		grid.set(player, newPos);
		return player;
	}
}
