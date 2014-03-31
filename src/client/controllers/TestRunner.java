package client.controllers;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import common.events.Event;
import common.events.ConnectAcceptedEvent;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;
import common.events.PlayerDeadEvent;


public class TestRunner extends PlayableClient implements Runnable{
	public static final long DEFAULT_WAIT_BETWEEN_ACTIONS = 0;
	private Collection<Event> receivedEvents;
	private ArrayList<Integer> events;
	private ArrayList<Long> timings;
	private boolean connected = false;
	private boolean dead = false;
	private List<Long> latencyList = new ArrayList<Long>();
	private long lastKeySent = System.currentTimeMillis();
	
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
			
		boolean timeout = false;
		while (!events.isEmpty() && i != events.size() && !timeout) {
			
			//wait for an amount of time specified next to the command in the test file
			//if none specified, it will be DEFAULT_WAIT_BETWEEN_ACTIONS
			if(timings.get(i) > 0){
				try {
					Thread.sleep(timings.get(i));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			//need to check if test player is dead or the game stopped here because of the waiting
			//game state might have changed while waiting
			if(!dead&& isGameRunning()){
				GameKeyEvent keyEvent = new GameKeyEvent(events.get(i));
				timeout = pressKeyAndWait(keyEvent.getKeyCode());
			} else{
				break;
			}
			i++;
		}
	}

	@Override
	protected void processPlayerDead(PlayerDeadEvent event) {
		if(event.player.playerId == this.playerId){
			dead = true;
			super.processPlayerDead(event);
		}
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
	
	
	
	public synchronized boolean pressKeyAndWait(int keyCode){
		Collection<GameKeyEventAck> wrongKeys = new ArrayList<>();
		
		pressKey(keyCode);
		
		GameKeyEventAck response = null;
		boolean found = false;
		while(!found){
			response = (GameKeyEventAck) waitFor(GameKeyEventAck.class);
			
			if(response == null){
				System.err.println("did not receive ack");
				return true; //timed out, did not receive the ack event
			}
			if (response.getKeyCode() != keyCode){
				wrongKeys.add(response);
			} else {
				found = true;
			}
		}
		
		receivedEvents.addAll(wrongKeys);
		notify();
		return false;
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
		if(event instanceof GameKeyEventAck) {
			long latency = System.currentTimeMillis() - lastKeySent;
			latencyList.add(latency);
			
			System.out.print("Player: " + this.playerId + " - Latency: "+ latency + " - Average Latency: " + getAverageLatency() + "\n");
		}
		
		if (receivedEvents == null){
			receivedEvents = new ArrayList<>();
		}
		receivedEvents.add(event);
		notify();
		return super.receive(event);
	}
	
	@Override
	public void pressKey(int keyCode){
		lastKeySent = System.currentTimeMillis();
		send(new GameKeyEvent(keyCode));
	}

	public long getAverageLatency(){
		long sum = 0;
		for(long l : latencyList){
			sum += l;
		}
		return sum/latencyList.size();
	}
	
	public List<Long> getLatencyList(){
		return latencyList;
	}
}
