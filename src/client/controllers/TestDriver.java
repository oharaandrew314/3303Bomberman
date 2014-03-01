package client.controllers;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import common.controllers.NetworkController;


public class TestDriver {
	public static final String[] TEST_FILE_NAMES = {"test1", "test2"};
	private Collection<TestCase> testcases;
	private NetworkController networkController;


	public TestDriver(NetworkController networkController){
		this.networkController = networkController;
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
	public void readTestCases(){
		for(String filename : TEST_FILE_NAMES){
			try {
				testcases.add(new TestCase(filename, networkController));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	

	
	
}


