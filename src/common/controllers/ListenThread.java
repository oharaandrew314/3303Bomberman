
package common.controllers;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ListenThread extends Thread {
    private static final int BUFFER_SIZE = 100000;
    
    private NetworkController networkController;
    private DatagramSocket socket;
    private boolean continueRunning = true;
    
    public ListenThread(NetworkController networkController) {
        this.networkController = networkController;
        this.socket = networkController.getSocket();
    }
    
    @Override
    public void run() {
        while(continueRunning) {
            DatagramPacket packet = new DatagramPacket(new byte[BUFFER_SIZE], BUFFER_SIZE);
            try {
                socket.receive(packet);
                networkController.receive(packet);
            } catch (IOException ex) {
                continueRunning = false;
            }
        }
    }
    
    public void stopListening() {
        continueRunning = false;
    }
}
