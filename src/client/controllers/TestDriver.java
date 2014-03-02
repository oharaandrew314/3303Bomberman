package client.controllers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import server.content.GridLoader;
import server.controllers.Server;
import common.controllers.NetworkController;
import common.models.Grid;


public class TestDriver {
	//public static final String[] TEST_FILE_NAMES = {"test1", "test2"};
	private Collection<TestCase> testcases;
	//private NetworkController[] networkControllers;


	public TestDriver(){
		//this.networkControllers = networkControllers;
		this.testcases = new ArrayList<TestCase>();
	}
	
	/**
	 * runs all the testCases specified by the filenames final value
	 */
	public void runAll(){
		for(TestCase test: testcases){
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
		String[] testfiles = {"test1" };
		driver.readTestCases(testfiles);
		driver.runAll();
	}
	

	
	
}


