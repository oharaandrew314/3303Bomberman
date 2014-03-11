package test.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.event.KeyEvent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import test.integration.helpers.MockClient;
import test.integration.helpers.MockServer;
import client.controllers.Client.State;

import common.events.ConnectRejectedEvent;

public class TestClient {
	
	private MockServer server;
	private MockClient client;

	@Before
	public void setUp() {
		server = new MockServer();
		assertEquals(0, server.getPlayers().size());
		client = new MockClient(true);
	}
	
	@After
	public void tearDown(){
		client.stop();
		server.stop();
	}

	@Test
	public void testConnect() {
		assertEquals(State.idle, client.getState());
		assertEquals(1, server.getPlayers().size());
	}
	
	@Test
	public void testDisconnect(){
		client.stop();
		client.waitFor(ConnectRejectedEvent.class);
		assertEquals(State.stopped, client.getState());
		assertEquals(0, server.getPlayers().size());
	}
	
	@Test
	public void testDisconnectByKey(){
		client.pressKey(KeyEvent.VK_ESCAPE);
		client.waitFor(ConnectRejectedEvent.class);
		assertEquals(State.stopped, client.getState());
		assertEquals(0, server.getPlayers().size());
	}
	
	@Test
	public void testStartGame(){
		assertTrue(!server.isGameRunning());
		server.newGame();
		client.startGame();
		assertTrue(server.isGameRunning());
	}
	
	@Test
	public void testDisconnectInGame(){
		server.newGame();
		client.startGame();
		testDisconnect();
	}
	
	@Test
	public void testDisconnectWithMultiplePlayers(){
		MockClient client2 = new MockClient(true);
		
		assertEquals(2, server.getPlayers().size());
		
		client2.stop();
		client2.waitFor(ConnectRejectedEvent.class);
		assertEquals(State.idle, client.getState());
		assertEquals(1, server.getPlayers().size());
		
		testDisconnect();
	}
}
