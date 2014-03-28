package common.controllers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import common.events.ConnectAcceptedEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;

public class NetworkController {
    public static final String LOCALHOST = "127.0.0.1";
    public static final int SERVER_PORT = 27001;
    
    private GameController gameController;
    private DatagramSocket socket;
    private ListenThread listener;
    protected Map<Integer, InetSocketAddress> peers;
    
    private boolean busy = false;
    private Object busyLock = new Object();
    
    private Queue<DatagramPacket> eventBuffer = new LinkedList<DatagramPacket>();
    
    public NetworkController(GameController gameController) {
        this.gameController = gameController;
        this.peers = new HashMap<>();
        
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @return the socket currently in use.
     */
    public DatagramSocket getSocket() {
        return socket;
    }
    
    /**
     * Allows the GameController to specify if network events should currently be handled.
     */
    public void setBusy(boolean busy) {
    	this.busy = busy;
    	if (!busy) {
    		synchronized(busyLock) {
    			busyLock.notify();
    		}
    	}
    }
    
    /**
     * Wait until the game controller is not busy and can process incoming events.
     */
    private void waitUntilReady() {
    	synchronized(busyLock) {
	    	while (busy) {
	    		try {
					busyLock.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
	    	}
    	}
    }
    
    /**
     * Start listening for messages on the given port.
     * @param port The port to listen on.
     */
    private void startListeningOn(int port) throws SocketException {
    	eventBuffer.clear();
        socket = new DatagramSocket(port);
        
        listener = new ListenThread(this);
        listener.start();
    }
    
    /**
     * Start listening for messages on the default server port.
     */
    public void startListeningOnServerPort() throws SocketException {
        startListeningOn(SERVER_PORT);
    }
    
    /**
     * Find an available port and listen on it.
     */
    public void startListeningOnAnyAvailablePort() {
        try {
        	startListeningOn(0);
        } catch (SocketException ex) {
        	Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Stop listening for messages and close the port.
     */
    public void stopListening() {
        if (listener != null)
            listener.stopListening();
        socket.close();
        eventBuffer.clear();
    }
    
    /**
     * Attempt to translate the given address and port into an InetAddress
     * and add it as a peer.
     * @param address address of the peer to add
     * @param port port of the peer to add
     */
    public void addPeer(String address, int port){
    	try {
            addPeer(new InetSocketAddress(InetAddress.getByName(address), port));
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Add a new peer, to which all messages will be sent.
     * @param peer The peer to add.
     */
    public synchronized void addPeer(InetSocketAddress peer) {
        peers.put( getPlayerIdFor(peer), peer);
    }
    
    /**
     * Add the local address & port combination where the server is expected to
     * be running as a local peer.
     */
    public void addLocalServerPeer() {
    	addPeer(LOCALHOST, SERVER_PORT);
    }
    
    /**
     * Send an event to all peers
     * @param event The event to send.
     */
    public synchronized void send(Event event) {
        try {
            for(InetSocketAddress peer : peers.values()) {
                sendToOnePeer(event, peer);
            }
        } catch (IOException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Send an event to only a single peer.
     * @param event The event to send.
     * @param peer The peer to send the event to.
     */
    private void sendToOnePeer(Event event, InetSocketAddress peer) throws IOException, SocketException {
        DatagramPacket packet = serialize(event);
        packet.setAddress(peer.getAddress());
        packet.setPort(peer.getPort());
        socket.send(packet);
    }
    
    public void requestAllEvents() {
    	while (!eventBuffer.isEmpty()) {
    		requestNextEvent();
    	}
    }
    
    public Event requestNextEvent() {
    	DatagramPacket data = null;
    	synchronized(this) {
    		data = eventBuffer.poll();
    	}
    	if (data == null) return null;
    	
    	Event event = null;
        try {
            event = deserialize(data);
            
            int playerId = getPlayerIdFor(data);
            event.setPlayerID(playerId);
            
            waitUntilReady();
            
            Event response = gameController.receive(event);
            if (response != null){
            	InetSocketAddress peer = new InetSocketAddress(
            		data.getAddress(), data.getPort()
            	);
            	if (response instanceof ConnectAcceptedEvent){
            		peers.put(playerId, peer);
            	} else if (response instanceof ConnectRejectedEvent){
            		peers.remove(playerId);
            	}
            	
            	response.setPlayerID(playerId);
            	try {
            		sendToOnePeer(response, peer);
            	} catch(SocketException e) {
            		stopListening();
            	}
            }
        } catch (ClassNotFoundException | IOException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return event;
    }
    
    /**
     * Called by the ListenerThread when a packet has been received.
     * The packet is queued for handling by the game controller.
     * @param data The incoming packet.
     */
    public synchronized void receive(DatagramPacket data) {
    	eventBuffer.add(data);
    }
    
    /**
     * @return the player ID from which the packet came. If this is a new peer,
     * the next ID is given.
     */
    private synchronized int getPlayerIdFor(DatagramPacket packet) {
    	InetSocketAddress peer = new InetSocketAddress(
    		packet.getAddress().getHostAddress(), packet.getPort()
    	);
    	return getPlayerIdFor(peer);
    }
    
    private synchronized int getPlayerIdFor(InetSocketAddress address){
    	int nextId = 1;
    	
    	// Search for existing peer
        for(Entry<Integer, InetSocketAddress> entry : peers.entrySet()) {
        	InetSocketAddress peer = entry.getValue();
        	nextId = Math.max(nextId, entry.getKey() + 1);
        	if(peer.equals(address)){
        		 return entry.getKey();
        	}
        }
        return nextId;  // If peer does not exist, assign next id
    }
    
    /**
     * Reset the peers this controller notifies.
     */
    public synchronized void clear(){
    	peers.clear();
    }
    
    /**
     * Serializes an event into a packet.
     */
    private DatagramPacket serialize(Event event) throws IOException {
        ByteArrayOutputStream serializerOutput = new ByteArrayOutputStream();
        ObjectOutputStream serializer = new ObjectOutputStream(serializerOutput);
        serializer.writeObject(event);
        serializer.flush();
        byte[] buffer = serializerOutput.toByteArray();
        
        return new DatagramPacket(buffer, buffer.length);
    }
    
    /**
     * De-serializes a packet back into an event.
     */
    private Event deserialize(DatagramPacket packet) throws IOException, ClassNotFoundException {
        ByteArrayInputStream baos = new ByteArrayInputStream(packet.getData());
        ObjectInputStream deserializer = new ObjectInputStream(baos);
        return (Event)deserializer.readObject();
    }
    
    public String getConnectionString(){
    	try {
			return InetAddress.getLocalHost() + ":" + socket.getLocalPort();
		} catch (UnknownHostException e) {
			return "Error: Unknown Address";
		}
    }
}
