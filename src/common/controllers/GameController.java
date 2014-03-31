package common.controllers;

import java.util.ArrayDeque;
import java.util.Observable;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

import server.controllers.SimulationListener;
import server.controllers.SimulationTimer;
import common.events.Event;
import common.models.Grid;
import common.views.AbstractView;

public abstract class GameController extends Observable implements SimulationListener {
	
	public static enum GameState {
		stopped, idle, newGame, gameRunning, stopping, error
	};
	
	private final ReentrantLock gridMutex;
	private final Queue<Event> undisplayedViewEvents;
	protected final NetworkController nwc;
	
	protected AbstractView view = null;
	protected Grid grid;
	protected GameState state = GameState.stopped;
	
	private final SimulationTimer timer;

	public GameController() {
		nwc = new NetworkController(this);
		gridMutex = new ReentrantLock();
		undisplayedViewEvents = new ArrayDeque<>();
		
		timer = new SimulationTimer();
		addListenerToTimer(this);
		timer.start();
	}
	
	protected final void addListenerToTimer(SimulationListener listener) {
		timer.addListener(listener);
	}
	protected final void removeListenerToTimer(SimulationListener listener){
		timer.removeListener(listener);
	}
	
	public final GridBuffer acquireGrid(){
		gridMutex.lock();
		return new GridBuffer(this, new Grid(grid));
	}
	
	public final Grid getGridCopy(){
		try(GridBuffer buf = acquireGrid()){
			return buf.grid;
		}
	}
	
	public final void applyGrid(Grid grid){
		this.grid = grid;
		if (gridMutex.isLocked()){
			gridMutex.unlock();
		}
	}
	
	public final GameState getState(){
		synchronized(state){
			return state;
		}
	}
	
	public final void setState(GameState state){
		synchronized(state){
			this.state = state;
		}
	}
	
	public final boolean isGameRunning(){
		return state == GameState.gameRunning;
	}
	
	public void setView(AbstractView view){
		this.view = view;
		for (Event event : undisplayedViewEvents){
			updateView(event);
		}
	}
	
	protected void send(Event event){
		nwc.send(event);
	}
	
	protected void updateView(Event event){
		if (view != null){
			view.handleEvent(state, event);
		} else {
			undisplayedViewEvents.add(event);
		}
	}
	
	protected void resetTimer(){
		timer.reset();
	}
	
	public void stop(){
		nwc.stopListening();
		nwc.clear();
		if (view != null){
			view.close();
		}
		grid = null;
		setState(GameState.stopped);
	}
	
	public String getConnectionString(){
		return nwc.getConnectionString();
	}
	
	public abstract Event receive(Event event);
	public abstract boolean isAcceptingConnections();
	
	public class GridBuffer implements AutoCloseable {
		
		public final Grid grid;
		private final GameController gc;

		public GridBuffer(GameController gc, Grid grid){
			this.gc = gc;
			this.grid = grid;
		}

		@Override
		public void close() {
			gc.applyGrid(grid);
		}
	}
}
