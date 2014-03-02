package client.controllers;

import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;

import server.content.GridLoader;
import server.controllers.Server;
import common.models.Grid;

public class TestTestDriver{

	
	private TestDriver driver;


	
	@Test
	public void testTestCases(){
		Grid grid = GridLoader.loadGrid("grid1.json");
		Server server = new Server();
		server.newGame(grid);
        driver = new TestDriver();
		String[] testfiles = {"test2" };
		driver.readTestCases(testfiles);
		driver.runAll();
		
	}
	

}
