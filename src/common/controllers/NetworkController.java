package common.controllers;

import common.events.ConnectAcceptedEvent;
import common.events.ConnectEvent;
import common.events.ConnectRejectedEvent;
import common.events.Event;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkController {
    public static final String LOCALHOST = "127.0.0.1";
    public static final int SERVER_PORT = 27001;
    
    private GameController gameController;
    private DatagramSocket socket;
    private ListenThread listener;
    private List<InetSocketAddress> peers;
    private boolean acceptNewPeers = false;
    
    public NetworkController(GameController gameController) {
        this.gameController = gameController;
        this.peers = new ArrayList<InetSocketAddress>();
        
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
     * Start listening for messages on the given port.
     * @param port The port to listen on.
     */
    public void startListeningOn(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        listener = new ListenThread(this);
        listener.start();
    }
    
    /**
     * Start listening for messages on the default server port.
     */
    public void startListeningOnServerPort() {
        startListeningOn(SERVER_PORT);
    }
    
    /**
     * Find an available port and listen on it.
     */
    public void startListeningOnAnyAvailablePort() {
        startListeningOn(0);
    }
    
    /**
     * Stop listening for messages and close the port.
     */
    public void stopListening() {
        if (listener != null)
            listener.stopListening();
        socket.close();
    }
    
    /**
     * @return true if the network controller will accept new ConnectEvents
     */
    public boolean isAcceptingNewPeers(){
    	return acceptNewPeers;
    }
    
    /**
     * Attempt to translate the given address and port into an InetAddress
     * and add it as a peer.
     * @param address address of the peer to add
     * @param port port of the peer ot add
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
    private void addPeer(InetSocketAddress peer) {
        peers.add(peer);
    }
    
    /**
     * Add the local address & port combination where the server is expected to
     * be running as a local peer.
     */
    public void addLocalServerPeer() {
    	addPeer(LOCALHOST, SERVER_PORT);
    }
    
    /**
     * Start automatically accepting new peers who send ConnectEvents.
     */
    public void acceptNewPeers() {
        acceptNewPeers = true;
    }
    
    /**
     * Start automatically rejecting new peers who send ConnectEvents.
     */
    public void rejectNewPeers() {
        acceptNewPeers = false;
    }
    
    /**
     * Send an event to all peers
     * @param event The event to send.
     */
    public synchronized void send(Event event) {
        try {
            for(InetSocketAddress peer : peers) {
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
    private void sendToOnePeer(Event event, InetSocketAddress peer) throws IOException {
        DatagramPacket packet = serialize(event);
        packet.setAddress(peer.getAddress());
        packet.setPort(peer.getPort());
        socket.send(packet);
    }
    
    /**
     * Called by the ListenerThread when a packet has been received.
     * If the packet contains a ConnectEvent, a reply is send with either
     * ConnectAcceptedEvent or ConnectRejectedEvent depending on the state of
     * acceptNewPeers.
     * @param data The incoming packet.
     */
    public synchronized void receive(DatagramPacket data) throws IOException {
        try {
            Event event = deserialize(data);
            
            event.setPlayerID(getPlayerIdFor(data));
            
            gameController.receive(event);
            
            if (event instanceof ConnectEvent) {
                InetSocketAddress peer = new InetSocketAddress(data.getAddress(), data.getPort());
                if (acceptNewPeers) {
                    sendToOnePeer(new ConnectAcceptedEvent(), peer);
                    peers.add(peer);
                } else {
                    sendToOnePeer(new ConnectRejectedEvent(), peer);
                }
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @return the player ID from which the packet came. If this is a new peer,
     * the next ID is given.
     */
    private int getPlayerIdFor(DatagramPacket packet) {
        for(int id = 0; id < peers.size(); id++) {
            InetSocketAddress peer = peers.get(id);
            if (peer.getHostString().equals(packet.getAddress().getHostAddress()) &&
                peer.getPort() == packet.getPort())
                return id;
        }
        return peers.size();
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
}
