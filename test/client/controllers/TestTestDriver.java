package client.controllers;


import org.junit.Test;



public class TestTestDriver{

	
	private TestDriver driver;


	
	@Test
	public void testTestCases(){
		//Grid grid = GridLoader.loadGrid("grid1.json");
		//Server server = new Server();
		//server.newGame(grid);
        driver = new TestDriver();
		String[] testfiles = {"test2" };
		driver.readTestCases(testfiles);
		
		//driver.runAll();
		
	}
	

}
