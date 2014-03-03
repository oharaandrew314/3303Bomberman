package test.integration;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.Server;
import test.integration.helpers.TestCase;

public class TestDriver {
	
	private Collection<TestCase> testcases;
	private Server server;
	
	@Before
	public void init(){
		init(new Server());
	}
	
	public void init(Server server){
		this.server = server;
		testcases = new ArrayList<TestCase>();
		String[] testfiles = {"test1", "test2", "test3" };
		readTestCases(testfiles);
	}
	
	/**
	 * runs all the testCases specified by the filenames final value
	 */
	@Test
	public void runAll(){
		server.reset();
		for(TestCase test: testcases){
			server.newGame(GridLoader.loadGrid("test/testGrid2.json"));
			test.run();
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
		run(null);
	}
	
	public static void run(Server server){		
		TestDriver driver = new TestDriver();
		driver.init(server == null ? new Server() : server);
		driver.runAll();
	}
	
	public Collection<TestCase> getTestCases(){
		return testcases;
	}
}
