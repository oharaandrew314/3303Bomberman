package common.controllers;

import java.util.Observable;
import java.util.concurrent.locks.ReentrantLock;

import common.events.Event;
import common.models.Grid;
import common.views.AbstractView;

public abstract class GameController extends Observable{
	
	public static enum GameState {
		stopped, idle, newGame, gameRunning, stopping, error
	};
	
	private final ReentrantLock gridMutex;
	protected final NetworkController nwc;
	protected AbstractView view;
	private Grid grid;
	private GameState state = GameState.stopped;

	public GameController() {
		nwc = new NetworkController(this);
		gridMutex = new ReentrantLock();
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
	}
	
	protected void send(Event event){
		nwc.send(event);
	}
	
	protected void updateView(Event event){
		if (view != null){
			view.handleEvent(state, event);
		}
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
