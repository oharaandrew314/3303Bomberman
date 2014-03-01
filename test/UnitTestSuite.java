import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import controllers.TestSimulationTimer;
import server.model.TestGrid;
import server.model.TestSquare;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   TestGrid.class,
   TestSquare.class,
   TestSimulationTimer.class,
})

public class UnitTestSuite {}