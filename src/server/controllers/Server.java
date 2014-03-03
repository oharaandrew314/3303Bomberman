package server.controllers;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import common.controllers.GameController;
import common.events.AcknowledgeKeyEvent;
import common.events.ConnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameStartEvent;
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
	
	private final SimulationTimer timer;
	private Map<Integer, Player> players;
	protected boolean running = false;
	private boolean requestReset = false;

	public Server() {
		timer = new SimulationTimer(this);
		players = new HashMap<>();
		addObserver(new TestLogger());
	}
	
	public void newGame(Grid grid){
		this.grid = grid;
		nwc.startListeningOnServerPort();
		nwc.acceptNewPeers();
	}
	
	@Override
	public boolean isGameRunning() {
		return running;
	}
	
	public boolean isAcceptingPlayers(){
		return nwc.isAcceptingNewPeers();
	}
	
	public void reset(){
		running = false;
		nwc.stopListening();
		nwc.clear();
		players.clear();
		grid = null;
		requestReset = false;
	}
	
	public void stop(){
		reset();
		timer.stop();
	}
	
	public synchronized void simulationUpdate(){		
		//TODO: Bomb logic
		//TODO: AI logic
		
		send(new ViewUpdateEvent(grid));
	}
	
	private void send(Event event){
		nwc.send(event);
		
		setChanged();
		notifyObservers(event);
	}

    @Override
    public synchronized void receive(Event event) {
    	send(event);
    	
    	int playerId = event.getPlayerID();
    	
    	// Accept ConntectEvent and add player to game
    	if (event instanceof ConnectEvent && !((ConnectEvent)event).spectator){
    		if (players.size() < MAX_PLAYERS){
                        nwc.acceptNewPeers();
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
    		} else {
    			nwc.rejectNewPeers();
    		}
    	}
    	
    	/*
    	 * Interpret GameKeyEvent.
    	 * 3 different key profiles: n00b, Righty, Southpaw
    	 */
    	else if (event instanceof GameKeyEvent){
    	   Player player = players.get(playerId);
    	   int keyCode = ((GameKeyEvent)event).getKeyCode();
    	   
    	   if (isGameRunning()){
    		   switch(keyCode){
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
    		   nwc.replyTo(event, new AcknowledgeKeyEvent());
    	   } else if (keyCode == KeyEvent.VK_ENTER){
    		   // Start game
    		   nwc.rejectNewPeers();
    		   running = true;
    		   
    		   send(new GameStartEvent());
    	   }
       }
    	
    	if (requestReset){
    		reset();
    	}
    }
    
    private void move(Player player, int dx, int dy){
    	// Do nothing if game is not running
    	if (!isGameRunning()){
    		return;
    	}
    	
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
				send(new PlayerDeadEvent(player));
				grid.remove(player);
    			
    			// If other unit was player, kill it
    			if (entity instanceof Player){
    				Player otherPlayer = (Player) entity;
    				players.remove(otherPlayer);
    				send(new PlayerDeadEvent(otherPlayer));
    				grid.remove(otherPlayer);
    			}
    		}
    	}
    	
    	// Check if player wins and notify views
    	for (Entity entity : grid.get(dest)){
    		if (entity instanceof Door){
    			send(new WinEvent(player, grid));
    			requestReset = true;
    		}
    	}
    }
    
    private void bomb(Player player){
    	// Do nothing if game is not running
    	if (!isGameRunning()){
    		return;
    	}
    	
    	// TODO: implement
    }

    public static void main(String[] args){
        Server server = new Server();
        server.newGame(CLAParser.parse(args));
        System.out.println("Server now running with initial grid of: ");
        System.out.println(server.grid.toString());
    }
}
