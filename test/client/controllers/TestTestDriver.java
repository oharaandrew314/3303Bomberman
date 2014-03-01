package client.controllers;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.Event;

public class TestTestDriver extends GameController{

	
	private TestDriver driver;// = new TestDriver(new NetworkController(this));

	@Before
    public void setUp() {
        driver = new TestDriver(new NetworkController(this));
        //clientB = new NetworkController(this);
        //server = new NetworkController(this);
        //receivedEvents = new LinkedList<>();
    }
	
	@Test
	public void testTestCases(){
		driver.readTestCases();
		driver.runAll();
		
	}
	
	@Override
	public void receive(Event event) {
		// TODO Auto-generated method stub
		
	}

}
