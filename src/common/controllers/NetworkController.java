package common.controllers;

import common.events.ConnectEvent;
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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NetworkController {
    public static final int DEFAULT_CLIENT_PORT = 27000;
    
    private static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 27001;
    
    private int port;
    private GameController gameController;
    private DatagramSocket socket;
    private ListenThread listener;
    private Collection<InetSocketAddress> peers;
    
    private ObjectOutputStream serializer;
    private ByteArrayOutputStream serializerOutput;
    
    public NetworkController(GameController gameController) {
        this.gameController = gameController;
        this.peers = new ArrayList<InetSocketAddress>();
        
        try {
            socket = new DatagramSocket();
        } catch (SocketException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            serializerOutput = new ByteArrayOutputStream();
            serializer = new ObjectOutputStream(serializerOutput);
        } catch (IOException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public DatagramSocket getSocket() {
        return socket;
    }
    
    public void startListeningOn(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        listener = new ListenThread(this);
        listener.start();
    }
    
    public void startListeningOnServerPort() {
        startListeningOn(SERVER_PORT);
    }
    
    public void startListeningOnDefaultClientPort() {
        startListeningOn(DEFAULT_CLIENT_PORT);
    }
    
    public void stopListening() {
        if (listener != null)
            listener.stopListening();
    }
    
    public void addPeer(InetSocketAddress peer) {
        peers.add(peer);
    }
    
    public void addServerPeer() {
        try {
            addPeer(new InetSocketAddress(InetAddress.getByName(SERVER_IP), SERVER_PORT));
        } catch (UnknownHostException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void send(Event event) {
        DatagramPacket packet = serialize(event);
        for(InetSocketAddress peerSocket : peers) {
            packet.setAddress(peerSocket.getAddress());
            packet.setPort(peerSocket.getPort());
            try {
                socket.send(packet);
            } catch (IOException ex) {
                Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void receive(DatagramPacket data) {
        Event event = deserialize(data);
        gameController.receive(event);
    }
    
    private DatagramPacket serialize(Event event) {
        try {
            serializer.writeObject(event);
            serializer.flush();
        } catch (IOException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        byte[] buffer = serializerOutput.toByteArray();
        return new DatagramPacket(buffer, buffer.length);
    }
    
    private Event deserialize(DatagramPacket packet) {
        try {
            ByteArrayInputStream baos = new ByteArrayInputStream(packet.getData());
            ObjectInputStream deserializer = new ObjectInputStream(baos);
            return (Event)deserializer.readObject();
        } catch (IOException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(NetworkController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
