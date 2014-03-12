package test.integration.helpers;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import client.controllers.PlayableClient;

import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.GameKeyEventAck;
import common.events.GameStartEvent;
import common.events.ViewUpdateEvent;

/**
 * Test Client
 * Logs events as they are received for testing
 * @author Andrew O'Hara
 *
 */
public class MockClient extends PlayableClient {
	
	public static final int TIMEOUT = 1000;
	private Collection<Event> events;
	
	public MockClient(boolean expectAccept){
		waitFor(expectAccept ? ConnectAcceptedEvent.class : ConnectRejectedEvent.class);
	}
	
	// Helpers
	
	public synchronized void pressKeyAndWait(int keyCode){
		Collection<GameKeyEventAck> wrongKeys = new ArrayList<>();
		
		pressKey(keyCode);
		
		GameKeyEventAck response = null;
		boolean found = false;
		while(!found){
			response = (GameKeyEventAck) waitFor(GameKeyEventAck.class);
			
			// If wrong event; add back to events
			if (response.getKeyCode() != keyCode){
				wrongKeys.add(response);
			} else {
				found = true;
			}
		}
		
		events.addAll(wrongKeys);
		notify();
	}
	
	public void waitForViewUpdate(){
		waitFor(ViewUpdateEvent.class);
	}
	
	public synchronized Event waitFor(Class<? extends Event> eventType){
		long start = System.currentTimeMillis();
		boolean timeout = false;
		Event event = search(eventType);

		while (event == null && !timeout){
			// Wait
			try {
				wait(TIMEOUT);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			// Check for timeout
			if (System.currentTimeMillis() - start > TIMEOUT){
				timeout = true;
			}
			
			event = search(eventType);
		}
		
		assertTrue("Client timed out waiting for response", !timeout);
		return event;
	}
	
	private Event search(Class<? extends Event> eventType){
		Event result = null;
		if (events != null){
			for (Event event : events){
				if (eventType.isInstance(event)){
					result = event;
					events.remove(result);
					break;
				}
			}
			
		}
		return result;
	}
	
	// Overrides
	
	@Override
	public synchronized Event receive(Event event){
		if (events == null){
			events = new ArrayList<>();
		}
		events.add(event);
		notify();
		return super.receive(event);
	}
	
	@Override
	public void startGame(){
		super.startGame();
		waitFor(GameStartEvent.class);
	}
}