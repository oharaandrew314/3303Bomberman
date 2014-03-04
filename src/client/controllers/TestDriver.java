package client.controllers;

import java.util.ArrayList;
import java.util.Collection;

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
		for(TestCase test: testcases){
			server.newGame(GridLoader.loadGrid("test/testGrid2.json"));
			test.run();
			server.endGame();
		}
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
		String[] testfiles = {"test1", "test2", "test3" };
		driver.readTestCases(testfiles);
		driver.runAll(new Server());
	}
	
	public Collection<TestCase> getTestCases(){
		return testcases;
	}
}
