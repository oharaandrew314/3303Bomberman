package test.controllers;

import static org.junit.Assert.*;

import org.junit.Test;

import server.controllers.Server;
import server.controllers.SimulationTimer;

public class TestSimulationTimer extends Server {

	private static final int
		TEST_DURATION = 1 * SimulationTimer.MS_IN_S,
		EXPECTED_UPDATES = TEST_DURATION / SimulationTimer.UPDATE_DELAY;
	
	private static final double ACCEPTABLE_ERROR_RATE = 0.2;
	
	private int numUpdates = 0;
	
	@Override
	public void simulationUpdate(){
		numUpdates++;
	}

	@Test
	public void test() {
		// Run the timer for some specified duration
		try {
			Thread.sleep(TEST_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// make sure number of actual updates was close enought to the expected
		double error = (
			((double) Math.max(numUpdates, EXPECTED_UPDATES)) /
			Math.min(numUpdates, EXPECTED_UPDATES)
		) - 1;
		assertTrue(error <= ACCEPTABLE_ERROR_RATE);
	}

}
