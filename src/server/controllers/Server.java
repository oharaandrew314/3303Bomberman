package server.controllers;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import common.controllers.GameController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
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
	public static enum State { stopped, idle, newGame, gameRunning };
	
	protected Map<Integer, Player> players;
	private State state = State.stopped;
	private final SimulationTimer timer;

	public Server() {
		players = new HashMap<>();
		addObserver(new TestLogger());
		
		timer = new SimulationTimer(this);
		nwc.startListeningOnServerPort();
		state = State.idle;
	}
	
	public void newGame(Grid grid){
		this.grid = grid;
		state = State.newGame;
	}
	
	@Override
	public boolean isGameRunning() {
		return state == State.gameRunning;
	}
	
	@Override
	public boolean isAcceptingConnections(){
		return state == State.newGame || state == State.idle;
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
	
	private void startGame(){
		// Place players
		List<Point> points = new ArrayList<>(grid.keySet());
		Random r = new Random();
		Queue<Player> queue = new ArrayDeque<>(players.values());
		while(!queue.isEmpty()){
			Point dest = points.get(r.nextInt(points.size() - 1));
			if (grid.isPassable(dest) && !grid.hasPlayer(dest)){
				grid.set(queue.remove(), dest);
			}
		}
		
		state = State.gameRunning;
		send(new GameStartEvent());
		timer.start();
	}
	
	public void endGame(){
		timer.stop();
		state = State.idle;
		grid = null;
	}
	
	public void stop(){
		endGame();
		timer.stop();
		nwc.stopListening();
		state = State.stopped;
	}

    @Override
    public synchronized Event receive(Event event) {
    	setChanged();
    	notifyObservers(event);
    	
    	int playerId = event.getPlayerID();
    	
    	// Decide whethere to accept or reject connection request
    	if (event instanceof ConnectEvent){
    		if (((ConnectEvent)event).spectator){
    			return new ConnectAcceptedEvent();
    		}
    		else if (isAcceptingConnections()){
    			if (players.size() < MAX_PLAYERS){
        			players.put(playerId, new Player(playerId));
        		}
    			return new ConnectAcceptedEvent();
    		}
    		return new ConnectRejectedEvent();
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
    	   } else if (keyCode == KeyEvent.VK_ENTER){
    		   startGame();
    	   }
    	   return event;
       }
    	return null;
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
    			endGame();
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
