package integration;

import integration.helpers.MockClient;
import integration.helpers.MockServer;

import java.awt.event.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import client.controllers.Spectator;
import client.views.JFrameTextView;

public class SpectatorTest {
	
	private Spectator spectator;
	private MockServer server;
	private MockClient client;
	

	@Before
	public void setUp() {
		server = new MockServer();
		server.newGame();
		
		client = MockClient.startMockClient(server);
		spectator = new Spectator(new JFrameTextView());
	}
	
	@After
	public void after(){
		server.reset();
	}

	@Test
	public void test() {
		client.pressKey(KeyEvent.VK_S);
		
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args){
		SpectatorTest test = new SpectatorTest();
		test.setUp();
		test.test();
		test.after();
	}
}
