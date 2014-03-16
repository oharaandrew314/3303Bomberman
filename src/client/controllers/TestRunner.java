package client.controllers;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

import common.controllers.GameController.GameState;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.ConnectAcceptedEvent;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
import common.events.PlayerDeadEvent;
import common.events.WinEvent;


public class TestRunner extends PlayableClient implements Runnable{
	private Collection<Event> receivedEvents;
	private ArrayList<Integer> events;
	private ArrayList<Long> timings;
	private boolean connected = false;
	private boolean dead = false;
	
	public TestRunner(ArrayList<Integer> events, ArrayList<Long> timings){
		this.events = events;
		this.timings = timings;
	}

	@Override
	protected void processConnectionAccepted(ConnectAcceptedEvent event) {
		connected = true;
		playerId = event.getAssignedPlayerId();
	}
	
	public void run(){
		int i = 0;
			
		while (!events.isEmpty() && i != events.size() && !dead&& isGameRunning()) {
			GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
			pressKeyAndWait(keyEvent.getKeyCode());
			
			
			//wait for an amount of time specified next to the command in the test file
			try {
				Thread.sleep(timings.get(i));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			i++;
		}
		//if(!dead){
		//	stop();
		//	nwc.stopListening();
		//}
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		dead = true;
		stop();
		nwc.stopListening();
		super.processPlayerDead(event);
	}

	@Override
	protected void endGame(WinEvent winEvent){
		stop();
		nwc.stopListening();
		super.endGame(winEvent);
	}
	
	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	public boolean isConnected(){
		return connected;
	}

	
	public void sendGameStartEvent(){
		GameKeyEvent startEvent = new GameKeyEvent(KeyEvent.VK_ENTER);
		send(startEvent);
	}
	
	
	
	public synchronized void pressKeyAndWait(int keyCode){
		Collection<GameKeyEventAck> wrongKeys = new ArrayList<>();
		
		pressKey(keyCode);
		
		GameKeyEventAck response = null;
		boolean found = false;
		while(!found){
			//System.out.println("waiting in PressKeyAndWait at waitFod(GameKeyEventAck.class");
			response = (GameKeyEventAck) waitFor(GameKeyEventAck.class);
			
			// If wrong event; add back to events
			if(response == null) return;
			if (response.getKeyCode() != keyCode){
				//System.out.println("found: " + response.getKeyCode() + " - expected: " + keyCode);
				wrongKeys.add(response);
			} else {
				found = true;
			}
		}
		
		receivedEvents.addAll(wrongKeys);
		notify();
	}
	
	public synchronized Event waitFor(Class<? extends Event> eventType){
		long start = System.currentTimeMillis();
		boolean timeout = false;
		Event event = search(eventType);

		while (event == null && !timeout){
			// Wait
			try {
				wait(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			// Check for timeout
			if (System.currentTimeMillis() - start > 1000){
				timeout = true;
			}
			
			event = search(eventType);
		}
		return event;
	}
	
	private Event search(Class<? extends Event> eventType){
		Event result = null;
		if (receivedEvents != null){
			for (Event event : receivedEvents){
				if (eventType.isInstance(event)){
					result = event;
					receivedEvents.remove(result);
					break;
				}
			}
			
		}
		return result;
	}
	
	@Override
	public synchronized Event receive(Event event){
		if (receivedEvents == null){
			receivedEvents = new ArrayList<>();
		}
		receivedEvents.add(event);
		notify();
		return super.receive(event);
	}
}
