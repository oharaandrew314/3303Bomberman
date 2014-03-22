package test.integration.helpers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import server.content.CreateGridException;
import server.content.GridLoader;
import server.controllers.Server;
import common.models.units.Player;

public class MockServer extends Server {
	
	public void newGame(){
		try {
			newGame(GridLoader.loadGrid("test/testGrid2.json"));
		} catch (CreateGridException e) {
			e.printStackTrace();
		}
	}
	
	public List<Player> getPlayers(){
		return new ArrayList<Player>(players.values());
	}
	
	public Player movePlayerTo(int playerId, Point newPos){
		Player player = players.get(playerId);
		
		try(GridBuffer buf = acquireGrid()){
			if (buf.grid.contains(player)){
				buf.grid.remove(player);
			}
			buf.grid.set(player, newPos);
		}
		
		return player;
	}
}
