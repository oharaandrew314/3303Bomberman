package controllers;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.KeyEvent;
import common.events.ViewUpdateEvent;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;

import org.junit.Test;

public class TestNetworkController extends GameController {
    private NetworkController clientA;
    private NetworkController clientB;
    private NetworkController server;
    
    private List<Event> receivedEvents;
    
    @Before
    public void setUp() {
        clientA = new NetworkController(this);
        clientB = new NetworkController(this);
        server = new NetworkController(this);
        receivedEvents = new LinkedList();
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
    
    @Test
    public void receiveShouldBeCalledOnGameControllerWhenEventIsReceived() {
        server.startListeningOnServerPort();
        try {
            clientA.addPeer(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), NetworkController.SERVER_PORT));
        } catch (UnknownHostException ex) {
            Logger.getLogger(TestNetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        clientA.send(new ConnectEvent());
        waitToReceiveEvents(1);
        assertTrue(receivedEvents.get(0) instanceof ConnectEvent);
    }
    
    @Test
    public void addServerPeerMethodShouldAddTheServerAddressAsAPeer() {
        server.startListeningOnServerPort();
        clientA.addServerPeer();
        clientA.send(new KeyEvent(0));
        waitToReceiveEvents(1);
        assertTrue(receivedEvents.get(0) instanceof KeyEvent);
    }
    
    @Test
    public void newPeersShouldBeSavedWhenAcceptNewPeersIsSet() {
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        clientA.addServerPeer();
        clientB.addServerPeer();
        
        server.startListeningOnServerPort();
        server.acceptNewPeers();
        
        clientA.send(new ConnectEvent());
        clientB.send(new ConnectEvent());
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
        clientA.addServerPeer();
        clientB.addServerPeer();
        server.startListeningOnServerPort();
        
        server.acceptNewPeers();
        clientA.send(new ConnectEvent());
        waitToReceiveEvents(2);
        
        server.rejectNewPeers();
        clientB.send(new ConnectEvent());
        waitToReceiveEvents(4);
                
        server.send(new ViewUpdateEvent(null));
        waitToReceiveEvents(5);
        
        assertReceivedNumberOfEvents(ConnectEvent.class, 2);
        assertReceivedNumberOfEvents(ConnectAcceptedEvent.class, 1);
        assertReceivedNumberOfEvents(ConnectRejectedEvent.class, 1);
        assertReceivedNumberOfEvents(ViewUpdateEvent.class, 1);
    }
}
