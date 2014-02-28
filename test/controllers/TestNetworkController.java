package controllers;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.ConnectAccepted;
import common.events.ConnectEvent;
import common.events.Event;
import java.util.LinkedList;
import java.util.List;
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
    public void receive(Event event) {
        receivedEvents.add(event);
    }
    
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
    
    @Test
    public void addServerPeerMethodShouldAddTheServerAddressAsAPeer() {
        server.startListeningOnServerPort();
        clientA.addServerPeer();
        clientA.send(new ConnectEvent());
        waitToReceiveEvents(1);
        assertTrue(receivedEvents.get(0) instanceof ConnectEvent);
    }
}
