import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import content.TestGridLoader;
import controllers.TestGridGenerator;
import controllers.TestNetworkController;
import controllers.TestSimulationTimer;
import events.TestSerializableKeyEvent;
import events.TestViewUpdateEvent;
import server.model.TestGrid;
import server.model.TestSquare;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestGrid.class,
   TestSquare.class,
   TestSimulationTimer.class,
   TestGridGenerator.class,
   TestNetworkController.class,
   TestSerializableKeyEvent.class,
   TestViewUpdateEvent.class,
   TestGridLoader.class,
})

public class UnitTestSuite {}