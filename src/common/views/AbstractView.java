package common.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import common.controllers.GameController;
import common.controllers.GameController.GameState;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.DisconnectEvent;
import common.events.Event;
import common.events.GameStartEvent;
import common.events.PlayerDeadEvent;
import common.events.PowerupReceivedEvent;
import common.events.ViewUpdateEvent;
import common.events.EndGameEvent;
import common.models.Grid;

public abstract class AbstractView extends WindowAdapter {
	
	public static final Dimension FRAME_SIZE = new Dimension(760, 760);
	public static final int GRID_SQUARE_SIZE = 30;
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	private final TextGenerator textGen;
	protected final JFrame frame;
	private final GameController gc;
	private final String connectionString;
	
	public AbstractView(GameController gc, TextGenerator textGen){
		this.textGen = textGen;
		connectionString = gc.getConnectionString();
		
		// Setup frame
		frame = new JFrame("Bomberman");
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(this);
		frame.setSize(FRAME_SIZE);
		
		initComponents();
		
		// Register as listener to GameController
		gc.setView(this);
		this.gc = gc;
		
		frame.setTitle(textGen.getTitle(connectionString, gc.getState()));
		frame.setVisible(true);
	}
	
	public void handleEvent(GameState state, Event event){
		String message = null;
		
		if (event instanceof ViewUpdateEvent){
			ViewUpdateEvent viewEvent = (ViewUpdateEvent) event;
			displayGrid(viewEvent.getGrid());
		}
		else if (event instanceof PlayerDeadEvent){
			PlayerDeadEvent deadEvent = (PlayerDeadEvent) event;
			message = textGen.getPlayerDead(deadEvent.player);
		}
		else if (event instanceof ConnectAcceptedEvent){
			message = textGen.getConnectionAccepted(event.getPlayerID());
		}
		else if (event instanceof ConnectRejectedEvent){
			message = textGen.getConnectionRejected();
		}
		else if (event instanceof EndGameEvent){
			EndGameEvent winEvent = (EndGameEvent) event;
			message = textGen.getEndGame(winEvent.player);
			displayGrid(winEvent.grid);
		}
		else if (event instanceof GameStartEvent){
			message = textGen.getStartGame();
		}
		else if (event instanceof DisconnectEvent){
			message = textGen.getPlayerDisconnected(event.getPlayerID());
		} else if (event instanceof PowerupReceivedEvent){
			PowerupReceivedEvent pEvent = (PowerupReceivedEvent) event;
			message = textGen.getPowerupMessage(pEvent.player, pEvent.powerup);
		}
		
		if (message != null){
			displayMessage(message);
		}
		frame.setTitle(textGen.getTitle(connectionString, state));
	}
	
	@Override
	public void windowClosing(WindowEvent event){
		close();
	}
	
	public void close(){
		if (frame.isVisible()){
			frame.setVisible(false);
			frame.dispose();
			gc.stop();
		}
	}
	
	public void addMenuBar(JMenuBar menuBar){
		if (menuBar != null){
			frame.setJMenuBar(menuBar);
		}
	}
	
	public void addKeyListener(KeyListener l) {
		frame.addKeyListener(l);
	}
	
	public Component getComponent(){
		return frame;
	}
	
	public abstract void displayMessage(String message);
	public abstract void displayGrid(Grid grid);
	protected abstract void initComponents();
}
