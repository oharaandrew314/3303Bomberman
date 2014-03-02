package client.controllers;
import java.util.ArrayList;
import java.util.Collection;





public class TestDriver {
	
	private Collection<TestCase> testcases;


	public TestDriver(){
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
		String[] testfiles = {"test2" };
		driver.readTestCases(testfiles);
		driver.runAll();
	}
	

	
	
}


