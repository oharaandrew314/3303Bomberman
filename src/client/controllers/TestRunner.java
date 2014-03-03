package client.controllers;

import java.util.ArrayList;
import java.awt.Point;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;


import common.events.GameKeyEvent;
import common.events.PlayerDeadEvent;
import common.models.Entity;
import common.models.Grid;


public class TestRunner extends Client implements Runnable{
	private static final Logger logger = Logger.getLogger(TestRunner.class);
	private ArrayList<Integer> events;
	private int playerNumber;
	private boolean connected = false;
	private boolean dead = false;
	
	public TestRunner(ArrayList<Integer> events, int playerNumber){
		BasicConfigurator.configure();
		this.events = events;
		this.playerNumber = playerNumber;
	}

	@Override
	protected void processViewUpdate(Grid grid2) {
		logger.debug("Player: " + playerNumber + " - UPDATE VIEW EVENT");
		if(grid != null){
			Point p1 = null,
				  p2 = null,
				  p1New = null,
				  p2New = null;
			for (Point p : grid.keySet()) {
				ArrayList<Entity> old = (ArrayList<Entity>) grid.get(p);
				ArrayList<Entity> newSquare = (ArrayList<Entity>) grid2.get(p);

				for (Entity e : old) {
					if (e.toString().equals("0")) {
						p1 = new Point(p);
					} else if (e.toString().equals("1")) {
						p2 = new Point(p);
					}
				}
				for (Entity e : newSquare) {
					if (e.toString().equals("0")) {
						p1New = new Point(p);
					} else if (e.toString().equals("1")) {
						p2New = new Point(p);
					}
				}

			}
			if(p1 != null && p1New != null && (p1.x != p1New.x || p1.y != p1New.y)){
				logger.info("Player 1 has moved from " +p1.x + "," + p1.y + " to " + p1New.x + "," + p1New.y);
			}
			if(p2 != null && p2New != null && (p2.x != p2New.x || p2.y != p2New.y)){
				logger.info("Player 2 has moved from " +p2.x + "," + p2.y + " to " + p2New.x + "," + p2New.y);
			}
		}
		else{
			logger.info("Player " + playerNumber + " first view of the grid");
		}
		this.grid = grid2;
	}

	@Override
	protected void processConnectionAccepted() {
		logger.debug("Player " + playerNumber + " Process Connection Accepted");
		connected = true;
	}

	@Override
	protected void processConnectionRejected() {
		logger.debug("Player " + playerNumber + " Process Connection Rejected");
	}
	
	public void run(){
		
		while(!connected){
			//wait
			waitForResponse(1);
			
		}
		GameKeyEvent startEvent = new GameKeyEvent(KeyEvent.VK_ENTER);
		nwc.send(startEvent);
		waitForResponse(100);
		int i = 0;
		while (!events.isEmpty() && i != events.size() && !dead&& isGameRunning()) {
			GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
			nwc.send(keyEvent);
			logger.debug("Player: " + playerNumber + " - SEND KEY EVENT");
			logger.info("Player " + playerNumber + "   "+ keyCodeToString(keyEvent));
			i++;
			waitForResponse(100);
		}
		waitForResponse(500);
		nwc.stopListening();
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		logger.debug("Player: " + playerNumber + " - PlayerDeadEvent");
		dead = true;
		nwc.stopListening();
	}


	
	private void waitForResponse(int millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String keyCodeToString(GameKeyEvent event){
		int code = event.getKeyCode();
		switch(code){
		case KeyEvent.VK_UP: return TestCase.UP;
		case KeyEvent.VK_DOWN: return TestCase.DOWN;
		case KeyEvent.VK_RIGHT: return TestCase.RIGHT;
		case KeyEvent.VK_LEFT: return TestCase.LEFT;
		default: return "undefined";
		}
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
}