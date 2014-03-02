package server.controllers;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import server.content.GridLoader;

import common.controllers.GameController;
import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.Player;
import common.models.Unit;

public class Server extends GameController {
	
	public static final int MAX_PLAYERS = 4;
	private Map<Integer, Player> players;
	private boolean running = true;

	public Server(Grid grid) {
		new SimulationTimer(this);
		players = new HashMap<>();
		this.grid = grid;
	}
	
	public void startListening(){
		nwc.startListeningOnServerPort();
		nwc.acceptNewPeers();
	}
	
	@Override
	public boolean isGameRunning() {
		return running;
	}
	
	public synchronized void simulationUpdate(){		
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
    			Player player = new Player(playerId);
    			players.put(playerId, player);
    			
    			// Find place on grid to add player
    			for (Point point : grid.keySet()){
    				if (grid.isPassable(point) && !grid.hasPlayer(point)){
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
    	   System.out.print(grid);
       }
    }
    
    private void move(Player player, int dx, int dy){
    	Point origin = grid.find(player);
    	
    	// Get destination point
    	Point dest = new Point(origin);
    	dest.translate(dx, dy);
    	
    	// Do not continue if player cannot move here
    	if (!grid.getPossibleMoves(origin).contains(dest)){
    		return;
    	}
    	
    	// Move player
    	grid.set(player, dest);
    	
    	// Check for collisions
    	for (Entity entity : grid.get(dest)){
    		if (entity instanceof Unit && !player.equals(entity)){
    			// Kill own player
    			players.remove(player);
				nwc.send(new PlayerDeadEvent(player));
				grid.remove(player);
    			
    			// If other unit was player, kill it
    			if (entity instanceof Player){
    				Player otherPlayer = (Player) entity;
    				players.remove(otherPlayer);
    				nwc.send(new PlayerDeadEvent(otherPlayer));
    				grid.remove(otherPlayer);
    			}
    		}
    	}
    	
    	// Check if player wins and notify views
    	for (Entity entity : grid.get(dest)){
    		if (entity instanceof Door){
    			nwc.send(new WinEvent(player));
    			running = false;
    		}
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
