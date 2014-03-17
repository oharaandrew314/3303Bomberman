package common.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JMenuBar;

import server.controllers.Server;
import client.controllers.PlayableClient;
import client.controllers.Spectator;
import client.views.ClientTextGenerator;
import client.views.SpectatorTextGenerator;
import common.controllers.GameController;
import common.models.Grid;

public class JFrameTextView extends AbstractView {
	
	public static final Dimension FRAME_SIZE = new Dimension(760, 760);
	public static final int GRID_TEXT_SIZE = 30;
	public static final String LINE_SEP = System.getProperty("line.separator");
	
	protected final JFrame frame;
	private final GameController gc;
	private TextArea textArea, console;
	private DoubleBufferedString doubleBufferedString = new DoubleBufferedString();
	
	public JFrameTextView(GameController gc, TextGenerator textGen){
		super(textGen);
		gc.setView(this);
		this.gc = gc;
		
		frame = new JFrame("Bomberman");
		frame.setLayout(new BorderLayout());
		frame.addWindowListener(this);
		frame.setSize(FRAME_SIZE);
		
		// Add Text Area
		textArea = new TextArea();
		textArea.setFont(new Font("monospaced", Font.PLAIN, GRID_TEXT_SIZE));
		textArea.setEditable(false);
		frame.add(textArea, BorderLayout.CENTER);
		
		// Add Console
		console = new TextArea();
		console.setEditable(false);
		frame.add(console, BorderLayout.SOUTH);
		
		frame.setVisible(true);
	}
	
	public void addMenuBar(JMenuBar menuBar){
		if (menuBar != null){
			frame.setJMenuBar(menuBar);
		}
	}
	
	@Override
	protected void setTitle(String title){
		frame.setTitle(title);
	}

	@Override
	public void close() {
		if (frame.isVisible()){
			frame.setVisible(false);
			frame.dispose();
			gc.stop();
		}
	}
	
	@Override
	public void displayGrid(Grid grid) {
		doubleBufferedString.write(grid.toString());
		textArea.setText(doubleBufferedString.read());
	}

	@Override
	public void addKeyListener(KeyListener l) {
		frame.addKeyListener(l);
		textArea.addKeyListener(l);
		console.addKeyListener(l);
	}
	
	@Override
	public void displayMessage(String message){
		console.append(message + LINE_SEP);
	}
	
	public Component getComponent(){
		return frame;
	}
	
	public static JFrameTextView newClientView(){
		PlayableClient client = new PlayableClient();
		JFrameTextView view = new JFrameTextView(
			client, new ClientTextGenerator()
		);
		view.addKeyListener(client);
		view.addMenuBar(MenuBarFactory.createClientMenuBar(view));
		return view;
	}
	
	public static JFrameTextView newServerView(Server server){
		JFrameTextView view = new JFrameTextView(
			server, new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createServerMenuBar(server, view));
		return view;
	}
	
	public static JFrameTextView newSpectatorView(){
		JFrameTextView view = new JFrameTextView(
			new Spectator(), new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createSpectatorMenuBar(view));
		return view;
	}
}