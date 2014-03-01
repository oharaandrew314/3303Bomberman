import integration.ServerTest;
import models.TestGrid;
import models.TestSquare;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import content.TestGridLoader;
import controllers.TestGridGenerator;
import controllers.TestNetworkController;
import controllers.TestSimulationTimer;
import events.TestSerializableKeyEvent;
import events.TestViewUpdateEvent;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	// Unit Tests
   TestGrid.class,
   TestSquare.class,
   TestSimulationTimer.class,
   TestGridGenerator.class,
   TestNetworkController.class,
   TestSerializableKeyEvent.class,
   TestViewUpdateEvent.class,
   TestGridLoader.class,
   
   //Integration Tests
   ServerTest.class,
})

public class UnitTestSuite {}