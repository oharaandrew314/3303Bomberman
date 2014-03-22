package common.views;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import server.controllers.Server;
import client.controllers.PlayableClient;
import client.controllers.Spectator;
import client.views.ClientTextGenerator;
import client.views.SpectatorTextGenerator;

import common.controllers.GameController;
import common.models.Grid;

public class TextView extends AbstractView {
	
	private TextArea textArea, console;
	
	public TextView(GameController gc, TextGenerator textGen){
		super(gc, textGen);
	}
	
	@Override
	protected void initComponents() {
		// Add Text Area
		textArea = new TextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, GRID_TEXT_SIZE));
		textArea.setEditable(false);
		frame.add(textArea, BorderLayout.CENTER);
		
		// Add Console
		console = new TextArea();
		console.setEditable(false);
		frame.add(console, BorderLayout.SOUTH);
	}
	
	@Override
	public void displayGrid(Grid grid) {
		textArea.setText(grid.toString());
	}

	@Override
	public void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		textArea.addKeyListener(l);
		console.addKeyListener(l);
	}
	
	@Override
	public void displayMessage(String message){
		console.append(message + LINE_SEP);
	}
	
	// Factory methods
	
	public static TextView newClientView(){
		PlayableClient client = new PlayableClient();
		TextView view = new TextView(
			client, new ClientTextGenerator()
		);
		view.addKeyListener(client);
		view.addMenuBar(MenuBarFactory.createClientMenuBar(view));
		return view;
	}
	
	public static TextView newServerView(Server server){
		TextView view = new TextView(
			server, new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createServerMenuBar(server, view));
		view.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		return view;
	}
	
	public static TextView newSpectatorView(){
		TextView view = new TextView(
			new Spectator(), new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createSpectatorMenuBar(view));
		return view;
	}
}