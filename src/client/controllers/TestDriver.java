package client.controllers;
import java.util.ArrayList;
import java.util.Collection;

import server.content.GridLoader;
import server.controllers.Server;





public class TestDriver {
	
	private Collection<TestCase> testcases;
	private Server server;;


	public TestDriver(){
		this.testcases = new ArrayList<TestCase>();
		server = new Server();
	}
	
	/**
	 * runs all the testCases specified by the filenames final value
	 */
	public void runAll(){
		for(TestCase test: testcases){
			server.newGame(GridLoader.loadGrid("grid1.json"));
			test.run();
		}
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
		driver.runAll();
	}
	

	
	
}


