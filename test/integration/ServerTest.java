package integration;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import common.events.ConnectEvent;
import common.models.Grid;
import server.content.GridLoader;
import server.controllers.Server;

public class ServerTest {
	
	private Server server;
	private Grid grid;

	@BeforeClass
	public void setUp() throws Exception {
		grid = GridLoader.loadGrid("grid1.json");
		server = new Server(grid);
	}

	@Test
	public void test() {
		server.receive(new ConnectEvent());
	}

}
