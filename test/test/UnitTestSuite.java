package test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import test.client.controllers.TestTestDriver;
import test.content.TestGridLoader;
import test.controllers.TestGridGenerator;
import test.controllers.TestNetworkController;
import test.controllers.TestSimulationTimer;
import test.events.TestGameKeyEvent;
import test.events.TestViewUpdateEvent;
import test.integration.ServerTest;
import test.integration.SystemTest;
import test.models.TestGrid;
import test.models.TestSquare;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Unit Tests
   TestGrid.class,
   TestSquare.class,
   TestGridGenerator.class,
   TestNetworkController.class,
   TestGameKeyEvent.class,
   TestViewUpdateEvent.class,
   TestGridLoader.class,
   TestTestDriver.class,
   TestSimulationTimer.class,
   
   //Integration Tests
   ServerTest.class,
   SystemTest.class,
})

public class UnitTestSuite {}