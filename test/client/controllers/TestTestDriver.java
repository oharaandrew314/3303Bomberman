package client.controllers;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import common.controllers.GameController;
import common.controllers.NetworkController;
import common.events.Event;

public class TestTestDriver extends GameController{

	
	private TestDriver driver;// = new TestDriver(new NetworkController(this));
	private NetworkController clientA;
	private NetworkController clientB;
	private NetworkController clientC;
	private NetworkController clientD;

	@Before
    public void setUp() {
		clientA = new NetworkController(this);
		clientB = new NetworkController(this);
		NetworkController[] netControllers = new NetworkController[2];
		netControllers[0] = clientA;
		netControllers[1] = clientB;
        driver = new TestDriver(netControllers);
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
