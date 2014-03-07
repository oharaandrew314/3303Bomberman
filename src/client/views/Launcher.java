package client.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

import client.controllers.PlayableClient;
import client.controllers.Spectator;
import server.content.GridLoader;
import server.controllers.Server;

public class Launcher extends WindowAdapter {
	
	private final JFrame frame;
	private Server server;
	private JTextField gridName;

	public Launcher() {
		frame = new JFrame("Bomberman");
		frame.setLayout(new GridLayout(3, 2));
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(new NewServerAction(this));
		add(new NewSpectatorAction());
		add(new NewClientAction());
		
		frame.add(gridName = new JTextField());
		add(new LoadNewGameAction(this));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private void add(Action action){
		frame.add(new JButton(action));
	}
	
	private void newServer(){
		server = new Server(new JFrameTextView());
	}
	
	@Override
	public void windowClosing(WindowEvent e){
		if (server != null){
			server.stop();
		}
	}
	
	public void loadNewGame(){
		server.newGame(GridLoader.loadGrid(gridName.getText()));
	}
	
	@SuppressWarnings("serial")
	private static class NewServerAction extends AbstractAction {
		
		private final Launcher launcher;
		
		public NewServerAction(Launcher launcher){
			super("New Server");
			this.launcher = launcher;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			launcher.newServer();
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewSpectatorAction extends AbstractAction {
		
		public NewSpectatorAction(){
			super("New Spectator");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new Spectator(new JFrameTextView());
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewClientAction extends AbstractAction {
		
		public NewClientAction(){
			super("New Client");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new PlayableClient(new JFrameTextView());
		}
	}
	
	public static void main(String[] args){
		new Launcher();
	}
	
	@SuppressWarnings("serial")
	private static class LoadNewGameAction extends AbstractAction {
		
		private final Launcher launcher;
		
		public LoadNewGameAction(Launcher launcher){
			super("Load Game");
			this.launcher = launcher;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			launcher.loadNewGame();
		}
	}

}
