package test.integration.helpers;

import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;

import client.controllers.Client;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameStartEvent;
import common.events.ViewUpdateEvent;
import common.models.Grid;

/**
 * Test Client
 * Logs events as they are received for testing
 * @author Andrew O'Hara
 *
 */
public class MockClient extends Client {
	
	public static final int TIMEOUT = 1000;
	private Collection<Event> events;
	
	public MockClient(boolean expectAccept){
		waitFor(expectAccept ? ConnectAcceptedEvent.class : ConnectRejectedEvent.class);
	}
	
	// Helpers
	
	public synchronized void pressKey(int keyCode){
		Collection<GameKeyEvent> wrongKeys = new ArrayList<>();
		
		nwc.send(new GameKeyEvent(keyCode));
		
		GameKeyEvent response = null;
		boolean found = false;
		while(!found){
			response = (GameKeyEvent) waitFor(GameKeyEvent.class);
			
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
	
	public void startGame(){
		pressKey(KeyEvent.VK_ENTER);
		waitFor(GameStartEvent.class);
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
		
		assertTrue("Client response timed out", !timeout);
		return event;
	}
	
	private Event search(Class<? extends Event> eventType){
		Event result = null;
		if (events != null){
			for (Event event : events){
				result = eventType.isInstance(event) ? event : null;
			}
			events.remove(result);
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
		return null;
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	@Override
	protected void processConnectionAccepted() {}
	@Override
	protected void processConnectionRejected() {}
	@Override
	protected void processViewUpdate(Grid grid) {}

}