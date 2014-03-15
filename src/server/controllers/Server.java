package server.controllers;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.net.SocketException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.ViewUpdateEvent;
import common.events.WinEvent;
import common.models.Bomb;
import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.Player;
import common.models.Unit;
import common.models.Enemy;

public class Server extends GameController implements SimulationListener {
	
	public static final int MAX_PLAYERS = 4;
	
	protected Map<Integer, Player> players;
	private final SimulationTimer timer;
	private final BombScheduler bombScheduler;
	private final AIScheduler aiScheduler;

	public Server(){
		players = new HashMap<>();
		addObserver(new TestLogger());
		 	
		timer = new SimulationTimer();
		timer.addListener(this);
		timer.addListener(bombScheduler = new BombScheduler(this));
		timer.addListener(aiScheduler = new AIScheduler(this));
		
		state = GameState.idle;
		
		try {
			nwc.startListeningOnServerPort();
			
		} catch (SocketException e) {
			Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, e);
			state = GameState.error;
		}
	}
	
	// Accessors
	
	@Override
	public boolean isAcceptingConnections(){
		return state == GameState.newGame || state == GameState.idle;
	}
	
	// State Methods
	
	public synchronized void newGame(Grid grid){
		if (state == GameState.idle && grid != null){
			this.grid = grid;
			
			// register enemies
			for (Point p : grid.keySet()){
				for (Entity e : grid.get(p)){
					if (e instanceof Enemy){
						aiScheduler.addEnemy((Enemy) e);
					}
				}
			}
			
			state = GameState.newGame;
			updateView(new ViewUpdateEvent(grid));
		} else {
			System.err.println("Could not create new game.");
		}
	}
	
	private void startGame(){
		if (state == GameState.newGame){
			// Place players
			List<Point> points = new ArrayList<>(grid.keySet());
			Random r = new Random();
			Queue<Player> queue = new ArrayDeque<>(players.values());
			while(!queue.isEmpty()){
				Point dest = points.get(r.nextInt(points.size() - 1));
				if (grid.get(dest).isEmpty()){ // place player on an empty spot
					grid.set(queue.remove(), dest);
				}
			}
			
			state = GameState.gameRunning;
			send(new GameStartEvent());
			timer.start();
		} else {
			System.err.println("Could not start game; not in new game state.");
		}
	}
	
	public synchronized void endGame(){
		if (state == GameState.gameRunning){
			timer.stop();
			state = GameState.idle;
			grid = null;
		} else {
			System.err.println("Could not end game; no game running");
		}
	}
	
	public synchronized void stop(){
		if (isGameRunning()){
			endGame();
		}
		super.stop();
		state = GameState.stopped;
	}
	
	// Other methods
	
	@Override
	public synchronized void simulationUpdate(){
		if (isGameRunning()){
			send(new ViewUpdateEvent(grid));
		}
	}
	
	@Override
    public void onTimerReset(){}
	
	private synchronized void killPlayer(Player player){
    	if (player == null){
    		throw new IllegalArgumentException("Player cannot be null.");
    	}
    	players.remove(player.playerId);
		send(new PlayerDeadEvent(player));
		grid.remove(player);
    }
	
	// Event Methods
	
	@Override
	protected void send(Event event){
		super.send(event);
		updateView(event);
		
		setChanged();
		notifyObservers(event);
	}

    @Override
    public synchronized Event receive(Event event) {
    	setChanged();
    	notifyObservers(event);
<<<<<<< HEAD
    	   	
    	// Decide whethere to accept or reject connection request
=======
    	
    	// Decide whether to accept or reject connection request
>>>>>>> dev
    	if (event instanceof ConnectEvent){
    		return handleConnectionRequest((ConnectEvent) event);
    	}
    	
    	/*
    	 * Interpret GameKeyEvent.
    	 * 3 different key profiles: n00b, Righty, Southpaw
    	 */
    	else if (event instanceof GameKeyEvent){
    	   Player player = players.get(event.getPlayerID());
    	   GameKeyEvent keyEvent = (GameKeyEvent) event;
    	   int keyCode = keyEvent.getKeyCode();
    	   
    	   // Handle DisconnectEvent at all states
    	   if (keyCode == KeyEvent.VK_ESCAPE){
    		   return disconnectPlayer(event);
    	   } 
    	   else if (isGameRunning()){
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
		   	   		case KeyEvent.VK_SEMICOLON: dropBombBy(player); break;
		   	   }
    	   } else if (keyCode == KeyEvent.VK_ENTER){
    		   System.out.println("received start event"); //TODO: delete
    		   startGame();
    	   }
    	   return new GameKeyEventAck(keyEvent);
       } else if (event instanceof DisconnectEvent){
    	   return disconnectPlayer(event);
       }
    	return null;
    }
    
    private Event handleConnectionRequest(ConnectEvent event){
    	int playerId = event.getPlayerID();
    	boolean accept = false;
    	
    	if (((ConnectEvent)event).spectator){
			accept = true;
		}
		else if (isAcceptingConnections()){
			if (players.size() < MAX_PLAYERS){
    			players.put(playerId, new Player(playerId));
    		}
			accept = true;
		}
    	
    	Event response = accept ? new ConnectAcceptedEvent() : new ConnectRejectedEvent();
    	response.setPlayerID(event.getPlayerID());
    	updateView(response);
    	return response;
    }
    
    private Event disconnectPlayer(Event event){
    	players.remove(event.getPlayerID()); // remove player from game
    	
    	Event notice = new DisconnectEvent();
    	notice.setPlayerID(event.getPlayerID());
    	send(notice); // Notify players of disconnect
    	
    	// Acknowledge disconnect
		return new ConnectRejectedEvent();
    }
    
    public synchronized void move(Unit unit, int dx, int dy){
    	// Do nothing if game is not running or player does not exist
    	if (!isGameRunning() || !grid.contains(unit)){
    		return;
    	}
    	
    	Point origin = grid.find(unit);
    	
    	// Get destination point
    	Point dest = new Point(origin);
    	dest.translate(dx, dy);
    	
    	// Do not continue if unit cannot move here
    	if (!grid.getPossibleMoves(origin).contains(dest)){
    		return;
    	}
    	
    	// Move unit
    	grid.set(unit, dest);
    	
    	// Check for collisions
    	for (Entity entity : grid.get(dest)){
    		if (entity instanceof Unit && !unit.equals(entity)){
    			// Kill own player
    			if (unit instanceof Player) {
    				killPlayer((Player) unit);
    			}
    			
    			// If other unit was player, kill it
    			if (entity instanceof Player){
    				killPlayer((Player) entity);	
    			}
    		}
    	}
    	
    	// Check if player wins and notify views
    	if (unit instanceof Player){
	    	for (Entity entity : grid.get(dest)){
	    		if (entity instanceof Door){
	    			send(new WinEvent((Player)unit, grid));
	    			endGame();
	    		}
	    	}
    	}
    }
    
    // Callback methods
    
    /**
     * For use by the AIController doing pathfinding.
     * This returns the nearest player
     */
    public synchronized Point getNearestPlayerLocation(Point source){
    	if (!isGameRunning()) return null; //players aren't on the board yet!
    	
    	Point minPoint = null;
    	int minPath = Integer.MAX_VALUE;
    	
    	for (Player player : players.values()){
    		Point loc = grid.find(player);
    		List<Point> path = getGrid().getShortestPath(source, loc);
    				
    		if (path != null && path.size() < minPath){
    			minPath = path.size();
    			minPoint = loc;
    		}
    	}
    	
    	return minPoint;
    }
    
    public synchronized Bomb dropBombBy(Player player){
    	Point loc = grid.find(player);
    	if (isGameRunning() && player.hasBombs() && !grid.hasTypeAt(Bomb.class, loc)){
    		Bomb bomb = player.getNextBomb();
    		grid.set(bomb, loc);
    		bombScheduler.scheduleBomb(bomb);
    		return bomb;
    	}
    	return null;
    }
    
<<<<<<< HEAD
    public Player movePlayerTo(int playerId, Point newPos){
		Player player = players.get(playerId);
		if (grid.contains(player)){
			grid.remove(player);
		}
		grid.set(player, newPos);
		return player;
	}
=======
    public synchronized void detonateBomb(Bomb bomb){
    	bomb.setDetonated();
		for (Point p : grid.getAffectedExplosionSquares(bomb)){
			for (Entity entity : grid.get(p)){
					
				// Kill any players in blast path
				if (entity instanceof Player){
					killPlayer((Player) entity);
				}
				
				// Detonate any bombs in blast path
				else if (entity instanceof Bomb && !((Bomb)entity).isDetonated()){
					detonateBomb((Bomb)entity);
				}
				
				else if (entity instanceof Enemy){
					aiScheduler.removeEnemy((Enemy) entity);
					grid.remove(entity);
				}
				
				// Remove any other destructible entities in blast path
				else if (entity.isDestructible()){
					grid.remove(entity);
				}
			}
		}
    }
    
    // Main method
>>>>>>> dev

    public static void main(String[] args){
        Server server = new Server();
        server.newGame(CLAParser.parse(args));
        System.out.println("Server now running with initial grid of: ");
        System.out.println(server.grid.toString());
    }
}
