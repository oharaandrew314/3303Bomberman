package client.controllers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameKeyEventAck;

public class PlayableClient extends Client implements KeyListener {
	
	private long lastKeySent = System.currentTimeMillis();
	private List<Long> latencyList = new ArrayList<Long>();
	
	public PlayableClient(){
		super();		
	}
	
	public PlayableClient(InetSocketAddress address){
		super(address);
	}

	@Override
	protected boolean isSpectator() {
		return false;
	}
	
	// Helpers
	
	public void pressKey(KeyEvent keyEvent){
		pressKey(keyEvent.getKeyCode());
	}
	
	public void pressKey(int keyCode){
		lastKeySent = System.currentTimeMillis();
		send(new GameKeyEvent(keyCode));
	}
	
	public void startGame(){
		pressKey(KeyEvent.VK_ENTER);
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	@Override
	public void keyReleased(KeyEvent e) {}

	@Override
	public void keyPressed(KeyEvent e) {
		pressKey(e);
	}
	
	@Override
	public Event receive(Event event) {
		if(event instanceof GameKeyEventAck) {
			long latency = System.currentTimeMillis() - lastKeySent;
			latencyList.add(latency);

			System.out.print("Player: " + this.playerId + " - Latency: "+ latency + " - Average Latency: " + getAverageLatency() + "\n");
		
			
		}
		return super.receive(event);
	}
	
	public long getAverageLatency(){
		long sum = 0;
		for(long l : latencyList){
			sum += l;
		}
		return sum/latencyList.size();
	}
	
	public List<Long> getLatencyList(){
		return latencyList;
	}
}
