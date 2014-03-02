package integration;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import server.controllers.Server;
import client.controllers.Client;
import client.controllers.Spectator;
import client.views.TextView;

public class SpectatorTest {
	
	private Spectator spectator;
	private Server server;
	private Client client;
	

	@Before
	public void setUp() throws Exception {
		spectator = new Spectator(new TextView());
		server = new Server();
	}

	@Test
	public void test() {
		fail("Not yet implemented");
	}

}
