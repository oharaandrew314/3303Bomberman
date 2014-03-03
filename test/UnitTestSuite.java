import integration.ServerTest;
import integration.SystemTest;
import models.TestGrid;
import models.TestSquare;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import client.controllers.TestTestDriver;
import content.TestGridLoader;
import controllers.TestGridGenerator;
import controllers.TestNetworkController;
import controllers.TestSimulationTimer;
import events.TestGameKeyEvent;
import events.TestViewUpdateEvent;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Unit Tests
   TestGrid.class,
   TestSquare.class,
   TestSimulationTimer.class,
   TestGridGenerator.class,
   TestNetworkController.class,
   TestGameKeyEvent.class,
   TestViewUpdateEvent.class,
   TestGridLoader.class,
   TestTestDriver.class,
   
   //Integration Tests
   ServerTest.class,
   SystemTest.class,
})

public class UnitTestSuite {}