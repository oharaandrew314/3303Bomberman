package test.integration.helpers;

import java.util.concurrent.Semaphore;

import server.content.GridLoader;
import server.controllers.Server;
import common.events.Event;
import common.events.GameKeyEvent;

public class MockServer extends Server {
	
	public final Semaphore keySem;
	
	public MockServer(){
		keySem = new Semaphore(1);
	}
	
	@Override
	public void receive(Event event){
		super.receive(event);
		if (event instanceof GameKeyEvent){
			keySem.release();
		}
	}
	
	public void newGame(){
		newGame(GridLoader.loadGrid("test/testGrid2.json"));
	}
}
