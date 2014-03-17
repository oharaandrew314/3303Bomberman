package client.controllers;

import java.util.ArrayList;
import java.util.Collection;

import server.content.CreateGridException;
import server.content.GridLoader;
import server.controllers.Server;

public class TestDriver {
	
	private Collection<TestCase> testcases;


	public TestDriver(){
		this.testcases = new ArrayList<TestCase>();
	}
	
	/**
	 * runs all the testCases specified by the filenames final value
	 */
	public void runAll(Server server){
		System.out.println("Number of tests: " + testcases.size());
		for(TestCase test: testcases){
			try {
				server.newGame(GridLoader.loadGrid(test.getGridFileName()));
			} catch (CreateGridException e) {
				e.printStackTrace();
			}
			test.run(server);
			if(server.isGameRunning()){
				server.endGame(null);
			}
		}
		System.out.println("Stopping the server");
		server.stop();
	}
	
	/**
	 * For each file, creates a testCase with all the events per player
	 */
	public void readTestCases(String[] testfiles){
		for(String filename : testfiles){
			testcases.add(new TestCase(filename));
		}
	}
	
	public static void main(String[] args){		
		TestDriver driver = new TestDriver();
		String[] testfiles = {"playerWin", "twoPlayerWin", "playerCollision", "testPlayerEnemyCollision", "breakWallWithBomb", "killPlayerWithBomb", "testHiddenDoor", "tryAndPlaceMultipleBombs", "place2BombsWithPowerUp", "testBombRangePlusOne", "testFlamePass", "testInvulnerability", "testUpgradedBombsChainReaction" };
		driver.readTestCases(testfiles);
		driver.runAll(new Server());
		System.exit(0);
	}
	
	public Collection<TestCase> getTestCases(){
		return testcases;
	}
}
