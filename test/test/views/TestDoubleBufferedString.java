package test.views;

import org.junit.*;

import common.views.DoubleBufferedString;
import static org.junit.Assert.*;

public class TestDoubleBufferedString {
	
	private class BasicReadThread extends Thread {
		DoubleBufferedString db;
		
		public BasicReadThread(DoubleBufferedString db) {
			this.db = db;
		}
		
		public void run() {
			assertEquals("test", db.read());
		}
	}
	
	private class BasicWriteThread extends Thread {
		DoubleBufferedString db;
		
		public BasicWriteThread(DoubleBufferedString db) {
			this.db = db;
		}
		
		public void run() {
			db.write("2");
		}
	}
	
	DoubleBufferedString db;
	
	@Before
	public void Setup() {
		db = new DoubleBufferedString();
	}
	
	@Test
	public void simpleWriteAndRead() {
		db.write("test");
		assertEquals("test", db.read());
	}
	
	@Test
	public void readIsBlockedUntilWrite() {
		(new BasicReadThread(db)).start();
		Thread.yield();
		db.write("test");
	}
	
	@Test
	public void writeIsBlockedUntilRead() {
		db.write("1");
		(new BasicWriteThread(db)).start();
		Thread.yield();
		assertEquals("1", db.read());
		assertEquals("2", db.read());
	}
}
