package server.controllers;

import java.awt.Point;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import server.models.ControlScheme;
import server.models.ControlScheme.Control;

import common.controllers.GameController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.EndGameEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.PowerupReceivedEvent;
import common.events.ViewUpdateEvent;
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
	private final BombScheduler bombScheduler;
	private final AIScheduler aiScheduler;

	public Server(){
		players = new HashMap<>();
		addObserver(new TestLogger());
		 	
		addListenerToTimer(bombScheduler = new BombScheduler(this));
		addListenerToTimer(aiScheduler = new AIScheduler(this));
		
		setState(GameState.idle);
		
		nwc.startListeningOnServerPort();
	}
	
	// Accessors
	
	@Override
	public boolean isAcceptingConnections(){
		GameState state = getState();
		return state == GameState.newGame || state == GameState.idle;
	}
	
	// State Methods
	
	public synchronized void newGame(Grid grid){
		if (getState() == GameState.idle && grid != null){
			applyGrid(grid);
			
			// register enemies
			for (Point p : grid.keySet()){
				for (Entity e : grid.get(p)){
					if (e instanceof Enemy){
						aiScheduler.addEnemy((Enemy) e);
					}
				}
			}
			
			setState(GameState.newGame);
			updateView(new ViewUpdateEvent(grid));
		} else {
			System.err.println("Could not create new game.");
		}
	}
	
	private synchronized void startGame(){
		if (getState() == GameState.newGame){
			// Place players
			try(GridBuffer buf = acquireGrid()){
				List<Point> points = new ArrayList<>(buf.grid.keySet());
				Random r = new Random();
				Queue<Player> queue = new ArrayDeque<>(players.values());
			
				while(!queue.isEmpty()){
					Point dest = points.get(r.nextInt(points.size() - 1));
					if (buf.grid.get(dest).isEmpty()){ // put player on empty spot
						buf.grid.set(queue.remove(), dest);
					}
				}
			}
			
			setState(GameState.gameRunning);
			send(new GameStartEvent());
		} else {
			System.err.println("Could not start game; not in new game state.");
		}
	}
	
	public synchronized void endGame(Player winner){
		if (getState() == GameState.gameRunning){
			endGame(getGridCopy(), winner);
		}
	}
	
	private synchronized void endGame(Grid grid, Player winner){
		if (getState() == GameState.gameRunning){
			setState(GameState.idle);
			send(new EndGameEvent(winner, grid));
			resetTimer();
		}
	}
	
	public synchronized void stop(){
		if (isGameRunning()){
			endGame(null);
		}
		super.stop();
	}
	
	// Other methods
	
	@Override
	public void simulationUpdate(long now){
		nwc.requestAllEvents();
		if (isGameRunning()){
			send(new ViewUpdateEvent(getGridCopy()));
		}
	}
	
	@Override
    public void onTimerReset(){}
	
	private void killUnit(GridBuffer buf, Unit unit){
    	if (unit == null){
    		throw new IllegalArgumentException("Player cannot be null.");
    	}
    	if(unit instanceof Player){
    		players.remove(((Player) unit).playerId);
    		send(new PlayerDeadEvent((Player) unit));
    		
    		// Check if game is over
        	if (players.isEmpty()){
        		endGame(null);
        	}
    	} else{
    		aiScheduler.removeEnemy((Enemy) unit);
    	}
    	buf.grid.remove(unit);
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
    	
    	return response;
    }
    
    private final Event handleGameKeyEvent(GameKeyEvent event){
    	Player player = players.get(event.getPlayerID());
    	Control control = ControlScheme.parse(event);

    	// Handle control
    	if (control == Control.Exit){
    		return disconnectPlayer(event);
    	} else if (control == Control.Start && !isGameRunning()){
    		startGame();
    	} else if (isGameRunning()){
    		switch(control){
	    		case Up: move(player, 0, -1); break;
	    		case Left: move(player, -1, 0); break;
	    		case Down: move(player, 0, 1); break;
	    		case Right: move(player, 1, 0); break;
	    		case Bomb: dropBombBy(player); break;
	    		default:
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
    	try(GridBuffer buf = acquireGrid()){
    		// Do nothing if game is not running or player does not exist
        	if (!isGameRunning() || !buf.grid.contains(unit)){
        		return;
        	}
        	
        	// Get destination point
        	Point origin = buf.grid.find(unit);
        	Point dest = new Point(origin.x + dx, origin.y + dy);
        	
        	// Do not continue if unit cannot move here
        	if (!buf.grid.getPossibleMoves(origin).contains(dest)){
        		return;
        	}
        	
        	// Move unit
        	buf.grid.set(unit, dest);
        	
        	// Check for collisions
        	for (Entity entity : buf.grid.get(dest)){
        		if (entity instanceof Unit && !unit.equals(entity)){
        			// Kill own unit
        			if(unit.canBeHurtBy(entity)){
        				killUnit(buf, unit);
        			}
        			
        			
        			// If other unit was unit, kill it
        			if (entity instanceof Unit){
        				if(((Unit) entity).canBeHurtBy(unit)){
        					killUnit(buf, (Unit) entity);	
        				}
        			}
        		}
        	}
        	
        	//check if player picks up a powerup
        	if(unit instanceof Player){
        		for(Entity entity : buf.grid.get(dest)){
            		if(entity instanceof Powerup){
            			//handle powerups
            			Player player = (Player) unit;
            			Powerup powerup = (Powerup) entity;
            			player.addPowerup(powerup);
            			buf.grid.remove(entity);
            			send(new PowerupReceivedEvent(player, powerup));
            		}
            	}
        	} 
        	
        	// Check if player landed on door and won the game
        	if (unit instanceof Player){
    	    	for (Entity entity : buf.grid.get(dest)){
    	    		if (entity instanceof Door){
    	    			endGame(buf.grid, (Player) unit);
    	    		}
    	    	}
        	}
    	}
    	
    }
    
    // Callback methods
    
    /**
     * For use by the AIController doing pathfinding.
     * This returns the nearest player
     */
    public Point getNearestPlayerLocation(Point source){
    	if (!isGameRunning()) return null; //players aren't on the board yet!
    	
    	Point minPoint = null;
    	int minPath = Integer.MAX_VALUE;
    	
    	Grid gridCopy = getGridCopy();
    	for (Player player : players.values()){
    		Point loc = gridCopy.find(player);
    		List<Point> path = gridCopy.getShortestPath(source, loc);
    				
    		if (path != null && path.size() < minPath){
    			minPath = path.size();
    			minPoint = loc;
    		}
    	}
    	
    	return minPoint;
    }
    
    public Bomb dropBombBy(Player player){
    	try(GridBuffer buf = acquireGrid()){
    		Point loc = buf.grid.find(player);
        	if (isGameRunning() && player.hasBombs() && !buf.grid.hasTypeAt(Bomb.class, loc)){
        		Bomb bomb = player.getNextBomb();
        		buf.grid.set(bomb, loc);
        		bombScheduler.scheduleBomb(bomb);
        		return bomb;
        	}
        	return null;
    	}
    }
    
    public Player getPlayer(int playerId){
    	return players.get(playerId);
    }
    
    public void detonateBomb(Bomb bomb){
    	try(GridBuffer buf = acquireGrid()){
    		detonateBomb(buf, bomb);
    	}
    }
    
    private void detonateBomb(GridBuffer buf, Bomb bomb){
    	bomb.setDetonated();
		for (Point p : buf.grid.getAffectedExplosionSquares(bomb)){
			for (Entity entity : buf.grid.get(p)){
					
				// Kill any players in blast path
				if (entity instanceof Unit){
					if(!((Unit) entity).isImmuneToBombs()){
						killUnit(buf, (Unit) entity);
					}
				}
				
				// Detonate any bombs in blast path
				else if (entity instanceof Bomb && !((Bomb)entity).isDetonated()){
					detonateBomb(buf, (Bomb)entity);
				}
				
				// Remove any other destructible entities in blast path
				else if (entity.isDestructible()){
					buf.grid.remove(entity);
				}
			}
		}
    }
    
    // Main method

    public static void main(String[] args){
        Server server = new Server();
        server.newGame(CLAParser.parse(args));
        System.out.println("Server now running with initial grid of: ");
        System.out.println(server.getGridCopy().toString());
    }
}
