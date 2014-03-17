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
import common.events.PowerupReceivedEvent;
import common.events.ViewUpdateEvent;
import common.events.EndGameEvent;
import common.models.Bomb;
import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.powerups.Powerup;
import common.models.units.Enemy;
import common.models.units.Player;
import common.models.units.Unit;

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
	
	public synchronized void endGame(Player winner){
		if (state == GameState.gameRunning){
			timer.stop();
			state = GameState.idle;
			send(new EndGameEvent(winner, grid));
			grid = null;
		}
	}
	
	public synchronized void stop(){
		if (isGameRunning()){
			endGame(null);
		}
		super.stop();
		state = GameState.stopped;
	}
	
	// Other methods
	
	@Override
	public synchronized void simulationUpdate(long now){
		if (isGameRunning()){
			nwc.setBusy(true);
			send(new ViewUpdateEvent(grid));
			nwc.setBusy(false);
		}
	}
	
	@Override
    public void onTimerReset(){}
	
	private synchronized void killUnit(Unit unit){
    	if (unit == null){
    		throw new IllegalArgumentException("Player cannot be null.");
    	}
    	if(unit instanceof Player){
    		players.remove(((Player) unit).playerId);
    		send(new PlayerDeadEvent((Player) unit));
    	} else{
    		aiScheduler.removeEnemy((Enemy) unit);
    	}
    	grid.remove(unit);
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
   	
    	Event response = null;

    	// Handle events
    	if (event instanceof ConnectEvent){
    		response =  handleConnectionRequest((ConnectEvent) event);
    	} else if (event instanceof GameKeyEvent){
    	   response = handleGameKeyEvent((GameKeyEvent) event);
       } else if (event instanceof DisconnectEvent){
    	   response = disconnectPlayer(event);
       }
    	
    	// Check if game-over
    	if (players.isEmpty()){
    		endGame(null);
    	}
    	
    	return response;
    }
    
    private final Event handleGameKeyEvent(GameKeyEvent event){
    	Player player = players.get(event.getPlayerID());
    	int keyCode = event.getKeyCode();

    	// Handle disconnect event
    	if (keyCode == KeyEvent.VK_ESCAPE){
    		return disconnectPlayer(event);
    	}
    	
    	// Handle start game event
    	else if (keyCode == KeyEvent.VK_ENTER && !isGameRunning()){
    		startGame();
    	}
    	
    	// Handle all other key events if game is running
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
    	} 
    	
    	// Acknowledge key event
    	return new GameKeyEventAck(event);
    }
    
    private final Event handleConnectionRequest(ConnectEvent event){
    	int playerId = event.getPlayerID();
    	boolean accept = false;
    	
    	if (((ConnectEvent)event).spectator){
			accept = true;
		}
		else if (isAcceptingConnections()){
			if (players.size() < MAX_PLAYERS){
    			players.put(playerId, new Player(playerId));
    			accept = true;
    		}		
		}
    	
    	Event response = accept ? new ConnectAcceptedEvent(playerId) : new ConnectRejectedEvent();
    	response.setPlayerID(event.getPlayerID());
    	updateView(response);
    	return response;
    }
    
    private final Event disconnectPlayer(Event event){
    	players.remove(event.getPlayerID()); // remove player from game
    	
    	Event notice = new DisconnectEvent();
    	notice.setPlayerID(event.getPlayerID());
    	send(notice); // Notify players of disconnect
    	
    	//if no players in game, end game
    	if (players.isEmpty()){
    		endGame(null);
    	}
    	
    	// Acknowledge disconnect
		return new ConnectRejectedEvent();
    }
    
    public final void move(Unit unit, int dx, int dy){    	
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
    			// Kill own unit
    			if(unit.canBeHurtBy(entity)){
    				killUnit(unit);
    			}
    			
    			
    			// If other unit was unit, kill it
    			if (entity instanceof Unit){
    				if(((Unit) entity).canBeHurtBy(unit)){
    					killUnit((Unit) entity);	
    				}
    			}
    		}
    	}
    	
    	//check if player picks up a powerup
    	if(unit instanceof Player){
    		for(Entity entity : grid.get(dest)){
        		if(entity instanceof Powerup){
        			//handle powerups
        			Player player = (Player) unit;
        			Powerup powerup = (Powerup) entity;
        			player.addPowerup(powerup);
        			grid.remove(entity);
        			send(new PowerupReceivedEvent(player, powerup));
        		}
        	}
    	} 
    	
    	// Check if player landed on door and won the game
    	if (unit instanceof Player){
	    	for (Entity entity : grid.get(dest)){
	    		if (entity instanceof Door){
	    			endGame((Player) unit);
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
    
    public Player getPlayer(int playerId){
    	return players.get(playerId);
    }
    
    public synchronized void detonateBomb(Bomb bomb){
    	synchronized(bomb){
	    	bomb.setDetonated();
			for (Point p : grid.getAffectedExplosionSquares(bomb)){
				for (Entity entity : grid.get(p)){
						
					// Kill any players in blast path
					if (entity instanceof Unit){
						if(!((Unit) entity).isImmuneToBombs()){
							killUnit((Unit) entity);
						}
					}
					
					// Detonate any bombs in blast path
					else if (entity instanceof Bomb && !((Bomb)entity).isDetonated()){
						detonateBomb((Bomb)entity);
					}
					
					// Remove any other destructible entities in blast path
					else if (entity.isDestructible()){
						grid.remove(entity);
					}
				}
			}
    	}
    }
    
    // Main method

    public static void main(String[] args){
        Server server = new Server();
        server.newGame(CLAParser.parse(args));
        System.out.println("Server now running with initial grid of: ");
        System.out.println(server.grid.toString());
    }
}
