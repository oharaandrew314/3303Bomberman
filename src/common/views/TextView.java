package common.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyListener;
import java.net.InetSocketAddress;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import server.controllers.Server;
import server.views.ServerTextGenerator;
import client.controllers.PlayableClient;
import client.controllers.Spectator;
import client.views.ClientTextGenerator;
import client.views.SpectatorTextGenerator;
import common.controllers.GameController;
import common.models.Grid;

public class TextView extends AbstractView {
	
	private JTextArea textArea;
	
	public TextView(GameController gc, TextGenerator textGen){
		super(gc, textGen);
	}
	
	@Override
	protected void initComponents() {
		// Add Text Area
		textArea = new JTextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, GRID_SQUARE_SIZE));
		textArea.setEditable(false);
		frame.add(textArea, BorderLayout.CENTER);
		
		// Enable Console
		setConsoleEnabled(true);
	}
	
	@Override
	public void displayGrid(Grid grid) {
		textArea.setText(grid.toString());
	}

	@Override
	public void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		textArea.addKeyListener(l);
	}
	
	// Factory methods
	
	public static TextView newClientView(InetSocketAddress address){
		PlayableClient client = new PlayableClient(address);
		TextView view = new TextView(
			client, new ClientTextGenerator()
		);
		view.addKeyListener(client);
		view.addMenuBar(MenuBarFactory.createClientMenuBar(view));
		return view;
	}
	
	public static TextView newServerView(Server server){
		TextView view = new TextView(
			server, new ServerTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createServerMenuBar(server, view));
		view.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return view;
	}
	
	public static TextView newSpectatorView(InetSocketAddress address){
		TextView view = new TextView(
			new Spectator(address), new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createSpectatorMenuBar(view));
		return view;
	}
}