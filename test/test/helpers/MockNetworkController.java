package test.helpers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayDeque;
import java.util.Queue;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectEvent;
import common.events.Event;

public class MockNetworkController extends NetworkController {
	private final Queue<Event> events;
	
	public MockNetworkController(GameController gc){
		super(gc);
		events = new ArrayDeque<>();
	}
	
	public boolean hasHandled(Class<? extends Event> type) {
		for(Event e : events) {
			if (type.isInstance(e))
				return true;
		}
		return false;
	}
	
	public Event connectAndWait(MockNetworkController dest){
		return sendAndWait(new ConnectEvent(false), dest);
	}
	
	public synchronized int getNumPeers(){
		return peers.size();
	}
	
	public synchronized Event waitFor(Class<? extends Event> waitingFor){
		Event e = null;
		boolean found = false;
		try {
			while(!found) {
				if (events.size() == 0){
					wait();
				}
				
				while(!found && events.size() > 0){
					e = events.remove();
					found = waitingFor.isInstance(e);
				}				
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
		return e;
	}
	
	public synchronized Event sendAndWait(Event event, MockNetworkController dest){
		send(event);
		return dest.waitFor(event.getClass());
	}
	
	public void removePeer(int playerId){
		peers.remove(playerId);
	}

	@Override
	public synchronized Event receive(DatagramPacket data) throws IOException {
		Event event = super.receive(data);
		events.add(event);
		notify();
		return event;
	}
}