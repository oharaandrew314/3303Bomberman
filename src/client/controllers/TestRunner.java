package client.controllers;

import java.util.Collection;

import common.controllers.NetworkController;
import common.events.KeyEvent;


public class TestRunner extends Thread{
	
	private NetworkController netController;
	private Collection<Integer> events;
	private int playerNumber;
	
	public TestRunner(NetworkController netController, Collection<Integer> events, int playerNumber){
		this.netController = netController;
		this.events = events;
		this.playerNumber = playerNumber;
	}
	
	/**
	 * Generates KeyEvents based on the keycodes attained from the testCase
	 * and sends the events to the NetworkController
	 */
	@Override
	public void run(){
		for(Integer i: events){
			KeyEvent key = new KeyEvent(i);
			System.out.println("Player: " + playerNumber + " - " + key.getKeyCode());
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//netController.send(key)
		}
	}
}
