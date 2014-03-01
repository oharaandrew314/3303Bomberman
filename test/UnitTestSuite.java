import models.TestGrid;
import models.TestSquare;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import content.TestGridLoader;
import controllers.TestGridGenerator;
import controllers.TestNetworkController;
import controllers.TestSimulationTimer;
import events.TestGameKeyEvent;
import events.TestViewUpdateEvent;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestGrid.class,
   TestSquare.class,
   TestSimulationTimer.class,
   TestGridGenerator.class,
   TestNetworkController.class,
   TestGameKeyEvent.class,
   TestViewUpdateEvent.class,
   TestGridLoader.class,
})

public class UnitTestSuite {}