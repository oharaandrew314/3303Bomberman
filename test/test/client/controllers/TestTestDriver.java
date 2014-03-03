package test.client.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import server.controllers.Server;
import test.integration.TestDriver;

public class TestTestDriver{
	
	private TestDriver driver;
	
	@Test
	public void testTestCases(){
        driver = new TestDriver(new Server());
		String[] testfiles = {"test1", "test3", "test2" };
		driver.readTestCases(testfiles);
		assertEquals(3, driver.getTestCases().size());
	}
}