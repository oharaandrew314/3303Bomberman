package integration;

import static org.junit.Assert.*;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.KeyEvent;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.text.Element;

import integration.helpers.MockClient;
import integration.helpers.MockServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.models.Grid;
import common.models.Player;
import client.controllers.Spectator;
import client.views.JFrameTextView;
import client.views.TextView;
import client.views.View;

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
		
		//server.
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
