package test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.helpers.MockNetworkController;

import common.controllers.GameController;
import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
import common.events.GameKeyEvent;
import common.events.ViewUpdateEvent;

public class TestNetworkController extends GameController {
    private MockNetworkController clientA;
    private MockNetworkController clientB;
    private MockNetworkController server;
    
    private boolean isAcceptingConnections;
    
    @Before
    public void setUp() {
        clientA = new MockNetworkController(this);
        clientB = new MockNetworkController(this);
        server = new MockNetworkController(this);
        isAcceptingConnections = true;
    }
    
    @After
    public void tearDown() {
        clientA.stopListening();
        clientB.stopListening();
        server.stopListening();
    }
    
    @Override
    public synchronized Event receive(Event event) {
        if (event instanceof ConnectEvent){
        	return (
        		isAcceptingConnections() ?
        		new ConnectAcceptedEvent() : new ConnectRejectedEvent()
        	);
        }
        return null;
    }
    
    @Override
	public boolean isGameRunning() {
		return true;
	}
    
    @Override
	public boolean isAcceptingConnections() {
		return isAcceptingConnections;
	}
    
    @Test
    public void receiveShouldBeCalledOnGameControllerWhenEventIsReceived() {
        server.startListeningOnServerPort();
        clientA.addLocalServerPeer();
        
        Event received = clientA.connectAndWait(server);
        assertTrue(received instanceof ConnectEvent);
    }
    
    @Test
    public void addServerPeerMethodShouldAddTheServerAddressAsAPeer() {
        server.startListeningOnServerPort();
        clientA.addLocalServerPeer();
        
        Event received = clientA.sendAndWait(new GameKeyEvent(0), server);
        assertTrue(received instanceof GameKeyEvent);
    }
    
    @Test
    public void newPeersShouldBeSavedWhenControllerAcceptsPeer() {
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        
        server.startListeningOnServerPort();
        
        clientA.send(new ConnectEvent(false));
        clientB.send(new ConnectEvent(false));
        server.waitFor(ConnectEvent.class);
        server.waitFor(ConnectEvent.class);
                
        server.send(new ViewUpdateEvent(null));
        clientA.waitFor(ViewUpdateEvent.class);
        clientB.waitFor(ViewUpdateEvent.class);
        
        assertEquals(2, server.getNumPeers());
    }
    
    @Test
    public void newPeersShouldNotBeSavedWhenRejectNewPeersIsSet() {
    	isAcceptingConnections = true;
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        server.startListeningOnServerPort();
        
        clientA.connectAndWait(server);
        isAcceptingConnections = false;
        clientB.connectAndWait(server);
        assertEquals(1, server.getNumPeers());
    }
    
    @Test
    public void receivedEventsShouldIncludePlayerIDs() {
        clientA.startListeningOn(1);
        clientB.startListeningOn(2);
        server.startListeningOnServerPort();
        
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();

        // Connect client A
        Event received = clientA.connectAndWait(server);
        assertEquals(0, received.getPlayerID());
        
        // Connect client B
        received = clientB.connectAndWait(server);
        assertEquals(1, received.getPlayerID());

        // Send a key event from a client
        received = clientB.sendAndWait(new GameKeyEvent(42), server);
        assertEquals(1, received.getPlayerID());
    }
    
    @Test
    public void clientsShouldBeAbleToListenOnAnyAvailablePort() {
        clientA.startListeningOnAnyAvailablePort();
        clientB.startListeningOnAnyAvailablePort();
        clientA.addLocalServerPeer();
        clientB.addLocalServerPeer();
        
        server.startListeningOnServerPort();
        
        clientA.send(new ConnectEvent(false));
        clientB.send(new ConnectEvent(false));
        server.waitFor(ConnectEvent.class);
        server.waitFor(ConnectEvent.class);
        
        assertEquals(2, server.getNumPeers());
    }
}
