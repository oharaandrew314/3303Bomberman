package test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.ViewUpdateEvent;

public class TestNetworkController extends GameController {
    private NetworkController clientA;
    private NetworkController clientB;
    private NetworkController server;
    
    private List<Event> receivedEvents = new LinkedList<Event>();
    
    @Before
    public void setUp() {
        clientA = new NetworkController(this);
        clientB = new NetworkController(this);
        server = new NetworkController(this);
        resetReceivedEvents();
    }
    
    @After
    public void tearDown() {
        clientA.stopListening();
        clientB.stopListening();
        server.stopListening();
    }
    
    @Override
    public synchronized void receive(Event event) {
        receivedEvents.add(event);
    }
    
    @Override
	public boolean isGameRunning() {
		return true;
	}
    
    /**
     * Enter a busy waiting state until the receivedEvents list reaches the 
     * given size. If we have waited for more than 5 seconds, we assume the
     * packets are not coming and mark the test as a failure.
     * @param count The expected size of the receivedEvent list.
     */
    private void waitToReceiveEvents(int count) {
        long start = System.currentTimeMillis() / 1000;
        boolean giveUp = false;
        while (receivedEvents.size() < count && !giveUp) {
            long current = System.currentTimeMillis() / 1000;
            if (current - start > 5) {
                giveUp = true;
                assertTrue("Gave up waiting for events after 5 seconds", false);
            }
        }
    }
    
    /**
     * Used to check that the correct number of events of a certain type were
     * received. Use this rather than checking the receivedEvents list directly
     * as the order of the list is unreliable.
     * @param type The type of event to count.
     * @param expected The number of times the event should have been received.
     */
    private void assertReceivedNumberOfEvents(Class<?> type, int expected) {
        int actual = 0;
        for(Event event : receivedEvents) {
            if (type.isInstance(event))
                actual++;
        }
        assertEquals(expected, actual);
    }
    
    private void resetReceivedEvents() {
        receivedEvents.clear();
    }
    
    @Test
    public void receiveShouldBeCalledOnGameControllerWhenEventIsReceived() {
        server.startListeningOnServerPort();
        clientA.addLocalServerPeer();
        clientA.send(new ConnectEvent(false));
        waitToReceiveEvents(1);
        assertTrue(receivedEvents.get(0) instanceof ConnectEvent);
    }
    
    @Test
    public void addServerPeerMethodShouldAddTheServerAddressAsAPeer() {
        server.startListeningOnServerPort();
        clientA.addLocalServerPeer();
        clientA.send(new GameKeyEvent(0));
        waitToReceiveEvents(1);
        assertTrue(receivedEvents.get(0) instanceof GameKeyEvent);
    }
    
    @Test
    public void newPeersShouldBeSavedWhenAcceptNewPeersIsSet() {
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        
        server.startListeningOnServerPort();
        server.acceptNewPeers();
        
        clientA.send(new ConnectEvent(false));
        clientB.send(new ConnectEvent(false));
        waitToReceiveEvents(4);
                
        server.send(new ViewUpdateEvent(null));
        waitToReceiveEvents(6);
        
        assertReceivedNumberOfEvents(ConnectEvent.class, 2);
        assertReceivedNumberOfEvents(ConnectAcceptedEvent.class, 2);
        assertReceivedNumberOfEvents(ConnectRejectedEvent.class, 0);
        assertReceivedNumberOfEvents(ViewUpdateEvent.class, 2);
    }
    
    @Test
    public void newPeersShouldNotBeSavedWhenRejectNewPeersIsSet() {
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        server.startListeningOnServerPort();
        
        server.acceptNewPeers();
        clientA.send(new ConnectEvent(false));
        waitToReceiveEvents(2);
        
        server.rejectNewPeers();
        clientB.send(new ConnectEvent(false));
        waitToReceiveEvents(4);
                
        server.send(new ViewUpdateEvent(null));
        waitToReceiveEvents(5);
        
        assertReceivedNumberOfEvents(ConnectEvent.class, 2);
        assertReceivedNumberOfEvents(ConnectAcceptedEvent.class, 1);
        assertReceivedNumberOfEvents(ConnectRejectedEvent.class, 1);
        assertReceivedNumberOfEvents(ViewUpdateEvent.class, 1);
    }
    
    @Test
    public void receivedEventsShouldIncludePlayerIDs() {
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        server.startListeningOnServerPort();
        
        server.acceptNewPeers();
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        
        clientA.send(new ConnectEvent(false));
        waitToReceiveEvents(2);
        
        assertEquals(0, receivedEvents.get(0).getPlayerID());
        assertEquals(0, receivedEvents.get(1).getPlayerID());
        resetReceivedEvents();
        
        clientB.send(new ConnectEvent(false));
        waitToReceiveEvents(2);
        
        //bit hacky, but prevents interrmittant failures due to network unreliability
        assertEquals(receivedEvents.get(0) instanceof ConnectEvent ? 1 : 0, receivedEvents.get(0).getPlayerID());
        assertEquals(receivedEvents.get(1) instanceof ConnectEvent ? 1 : 0, receivedEvents.get(1).getPlayerID());
        resetReceivedEvents();
        
        clientB.send(new GameKeyEvent(42));
        waitToReceiveEvents(1);
        
        assertEquals(1, receivedEvents.get(0).getPlayerID());
    }
    
    @Test
    public void clientsShouldBeAbleToListenOnAnyAvailablePort() {
        clientA.startListeningOnAnyAvailablePort();
        clientB.startListeningOnAnyAvailablePort();
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        
        server.startListeningOnServerPort();
        server.acceptNewPeers();
        
        clientA.send(new ConnectEvent(false));
        clientB.send(new ConnectEvent(false));
        waitToReceiveEvents(4);
        
        assertReceivedNumberOfEvents(ConnectEvent.class, 2);
        assertReceivedNumberOfEvents(ConnectAcceptedEvent.class, 2);
    }
}
