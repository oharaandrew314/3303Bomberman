package test.controllers;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.controllers.Server;
import server.controllers.SimulationTimer;
import test.helpers.Condition;

public class TestSimulationTimer {
	
	private SimulationTimer timer;
	private TimerServer server;

	@Before
	public void setUp()  {
		server = new TimerServer();
		timer = new SimulationTimer(server);
	}

	@After
	public void tearDown() {
		server.stop();
	}
	
	@Test
	public void testRestart() {
		timer.start();
		server.waitForUpdate();
		
		timer.start();
		server.waitForUpdate();
		
		timer.stop();
		timer.start();
		server.waitForUpdate();
		
		timer.stop();
	}
	
	private class TimerServer extends Server {
		
		private final Condition condition;
		
		public TimerServer(){
			condition = new Condition();
		}
		
		@Override
		public void simulationUpdate(){
			condition.notifyCond();
		}
		
		public void waitForUpdate(){
			condition.waitCond();
		}
	}

}
