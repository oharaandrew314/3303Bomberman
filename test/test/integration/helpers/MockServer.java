package test.integration.helpers;

import java.awt.Point;
import java.util.List;
import java.util.concurrent.Semaphore;

import server.content.GridLoader;
import server.controllers.Server;
import common.events.Event;
import common.events.GameKeyEvent;
import common.models.Grid;
import common.models.Player;

public class MockServer extends Server {
	
	public final Semaphore keySem;
	
	public MockServer(){
		keySem = new Semaphore(1);
	}
	
	@Override
	public void receive(Event event){
		super.receive(event);
		if (event instanceof GameKeyEvent){
			keySem.release();
		}
	}
	
	public void newGame(){
		newGame(GridLoader.loadGrid("test/testGrid2.json"));
	}
	
	public Grid getGrid(){
		return grid;
	}
	
	public Player movePlayerTo(int playerId, int totalPlayers, Point newPos){
		List<Player> players =  IntegrationHelper.findPlayers(grid, totalPlayers);
		Player player = players.get(playerId);
		grid.set(player,  newPos);
		return player;
	}
}
