package test.client.controllers;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import client.controllers.TestDriver;

public class TestTestDriver{
	
	private TestDriver driver;
	
	@Test
	public void testTestCases(){
        driver = new TestDriver();
		driver.readTestCases(TestDriver.TESTFILES);
		assertEquals(TestDriver.TESTFILES.length, driver.getTestCases().size());
	}
}