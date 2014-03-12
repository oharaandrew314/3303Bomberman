package test.controllers;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import server.controllers.SimulationListener;
import server.controllers.SimulationTimer;
import test.helpers.Condition;

public class TestSimulationTimer {
	
	private SimulationTimer timer;
	private TimerListener listener;

	@Before
	public void setUp()  {
		ArrayList<SimulationListener> listeners = new ArrayList<>();
		listeners.add(listener = new TimerListener());
		timer = new SimulationTimer(listeners);
	}

	@After
	public void tearDown() {
		timer.stop();
	}
	
	@Test
	public void testRestart() {
		timer.start();
		listener.waitForUpdate();
		
		timer.start();
		listener.waitForUpdate();
		
		timer.stop();
		timer.start();
		listener.waitForUpdate();
		
		timer.stop();
	}
	
	private class TimerListener implements SimulationListener {
		
		private final Condition condition;
		
		public TimerListener(){
			condition = new Condition();
		}
		
		@Override
		public void simulationUpdate(){
			condition.notifyCond();
		}
		
		public void waitForUpdate(){
			condition.waitCond();
		}

		@Override
		public void onTimerReset() {
		}
	}

}
