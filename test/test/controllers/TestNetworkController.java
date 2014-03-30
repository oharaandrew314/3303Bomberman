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
    
    // Overrides
    
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
	public boolean isAcceptingConnections() {
		return isAcceptingConnections;
	}
    
    @Override
	public void stop() {}
    
    @Override
    public void simulationUpdate(long now) {}
    
    @Override
    public void onTimerReset() {}
    
    // Helpers
    
    private void startServer(MockNetworkController server) {
    	server.startListeningOnServerPort();
    }
    
    private void addServerToClient(MockNetworkController client){
    	client.startListeningOnAnyAvailablePort();
    	client.addLocalServerPeer();
    }
    
    private int getClientIdFromServer(MockNetworkController client){
    	Event response = client.sendAndWait(new GameKeyEvent(42), server);
        return response.getPlayerID();
    }
    
    // Tests

    @Test
    public void receiveShouldBeCalledOnGameControllerWhenEventIsReceived() {
        startServer(server);
        clientA.addLocalServerPeer();
        
        Event received = clientA.connectAndWait(server);
        assertTrue(received instanceof ConnectEvent);
    }
    
    @Test
    public void addServerPeerMethodShouldAddTheServerAddressAsAPeer() {
    	startServer(server);
        clientA.addLocalServerPeer();
        
        Event received = clientA.sendAndWait(new GameKeyEvent(0), server);
        assertTrue(received instanceof GameKeyEvent);
    }
    
    @Test
    public void newPeersShouldBeSavedWhenControllerAcceptsPeer() {
    	startServer(server);
    	addServerToClient(clientA);
    	addServerToClient(clientB);
        
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
    	startServer(server);
    	addServerToClient(clientA);
    	addServerToClient(clientB);
        
        clientA.connectAndWait(server);
        isAcceptingConnections = false;
        clientB.connectAndWait(server);
        assertEquals(1, server.getNumPeers());
    }
    
    @Test
    public void receivedEventsShouldIncludePlayerIDs() {
    	startServer(server);
    	addServerToClient(clientA);
    	addServerToClient(clientB);

        // Connect client A
        Event received = clientA.connectAndWait(server);
        assertEquals(1, received.getPlayerID());
        
        // Connect client B
        received = clientB.connectAndWait(server);
        assertEquals(2, received.getPlayerID());

        // Test player id
        assertEquals(2, getClientIdFromServer(clientB));
    }
    
    @Test
    public void testPeerIdAssignment(){
    	startServer(server);
        
        MockNetworkController clientC = new MockNetworkController(this);
        MockNetworkController clientD = new MockNetworkController(this);
        
        addServerToClient(clientA);
    	addServerToClient(clientB);
    	addServerToClient(clientC);
    	addServerToClient(clientD);
    	
    	clientA.connectAndWait(server);
    	clientB.connectAndWait(server);
    	clientC.connectAndWait(server);
    	clientD.connectAndWait(server);
    	
    	// Check ids
    	assertEquals(4, server.getNumPeers());
    	 assertEquals(1, getClientIdFromServer(clientA));
    	 assertEquals(2, getClientIdFromServer(clientB));
    	 assertEquals(3, getClientIdFromServer(clientC));
    	 assertEquals(4, getClientIdFromServer(clientD));
    	 
    	 //Remove one peer and ensure it is re-added after the existing peers
    	 server.removePeer(2);
    	 assertEquals(3, server.getNumPeers());
    	 clientB.connectAndWait(server);
    	 assertEquals(4, server.getNumPeers());
    	 assertEquals(5, getClientIdFromServer(clientB));
    	 
    	 // Remove all peers and ensure they are assigned ids starting from 0
    	 server.removePeer(1);
    	 server.removePeer(3);
    	 server.removePeer(4);
    	 server.removePeer(5);
    	 clientD.connectAndWait(server);
     	 clientA.connectAndWait(server);
     	 clientB.connectAndWait(server);
     	 clientC.connectAndWait(server);
     	assertEquals(1, getClientIdFromServer(clientD));
     	assertEquals(2, getClientIdFromServer(clientA));
     	assertEquals(3, getClientIdFromServer(clientB));
     	assertEquals(4, getClientIdFromServer(clientC));
    }
    
    @Test
    public void testNetworkControllerIsBlockedWhenGameControllerIsBusy() {
    	startServer(server);
    	addServerToClient(clientA);
    	server.setBusy(true);
    	clientA.send(new ConnectEvent(false));
    	try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	assertTrue(!server.hasHandled(ConnectEvent.class));
    	server.setBusy(false);
    	server.waitFor(ConnectEvent.class);
    }
}
