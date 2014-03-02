package server.controllers;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.content.GridLoader;
import common.controllers.GameController;
import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.models.Entity;
import common.models.Grid;
import common.models.Player;
import common.models.Unit;

public class Server extends GameController {
	
	public static final int MAX_PLAYERS = 4;
	private Map<Integer, Player> players;

	public Server(Grid grid) {
		new SimulationTimer(this);
		players = new HashMap<>();
		this.grid = grid;
	}
	
	public void startListening(){
		nwc.startListeningOnServerPort();
		nwc.acceptNewPeers();
	}
	
	public synchronized void simulationUpdate(){
		//Calculate collisions
		for (Point point : grid.keySet()){
			Set<Unit> units = new HashSet<>();
			for (Entity entity : grid.get(point)){
				if (entity instanceof Unit){
					units.add((Unit)entity);
				}
			}
			
			// If unit collision, process deaths
			if (units.size() > 2){
				for (Unit unit : units){
					if (unit instanceof Player){
						Player player = (Player) unit;
						nwc.send(new PlayerDeadEvent(player));
						players.remove(player);
					}
					grid.remove(unit); // Remove from grid
				}
			}
		}
		
		//TODO: Bomb logic
		//TODO: AI logic
		
		nwc.send(new ViewUpdateEvent(grid));
	}

    @Override
    public synchronized void receive(Event event) {
    	int playerId = event.getPlayerID();
    	
    	// Accept ConntectEvent and add player to game
    	if (event instanceof ConnectEvent){
    		if (players.size() < MAX_PLAYERS){
    			Player player = new Player("Player " + playerId);
    			players.put(playerId, player);
    			
    			// Find place on grid to add player
    			for (Point point : grid.keySet()){
    				if (grid.isPassable(point)){
    					grid.set(player, point);
    					return;
    				}
    			}
    			throw new RuntimeException("Could not find place to add player");
    		}
    	}
    	
    	/*
    	 * Interpret GameKeyEvent.
    	 * 3 different key profiles: n00b, Righty, Southpaw
    	 */
    	else if (event instanceof GameKeyEvent){
    	   Player player = players.get(playerId);
    	   
    	   switch(((GameKeyEvent)event).getKeyCode()){
    	   		case KeyEvent.VK_UP:
    	   		case KeyEvent.VK_W:
    	   		case KeyEvent.VK_I: move(player, 0, -1); break;
    	   		case KeyEvent.VK_LEFT:
    	   		case KeyEvent.VK_A:
    	   		case KeyEvent.VK_J: move(player, -1, 0); break;
    	   		case KeyEvent.VK_DOWN:
    	   		case KeyEvent.VK_S:
    	   		case KeyEvent.VK_K: move(player, 0, 1); break;
    	   		case KeyEvent.VK_RIGHT:
    	   		case KeyEvent.VK_D:
    	   		case KeyEvent.VK_L: move(player, 1, 0); break;
    	   		case KeyEvent.VK_SPACE:
    	   		case KeyEvent.VK_F:
    	   		case KeyEvent.VK_SEMICOLON: bomb(player); break;
    	   }
       }
    }
    
    private void move(Player player, int dx, int dy){
    	Point origin = grid.find(player);
    	
    	// Get destination point
    	Point dest = new Point(origin);
    	dest.translate(dx, dy);
    	
    	if (grid.getPossibleMoves(origin).contains(dest)){
    		grid.set(player, dest);
    	}
    }
    
    private void bomb(Player player){
    	// TODO: implement
    }

    public static void main(String[] args){
    	// FIXME: Default grid for now
    	Grid grid = GridLoader.loadGrid("grid1.json");
		Server server = new Server(grid);
		server.startListening();
	}
}
