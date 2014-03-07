package common.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import client.controllers.PlayableClient;
import client.controllers.Spectator;
import client.views.ClientJFrameTextView;
import client.views.SpectatorJFrameTextView;
import server.controllers.Server;
import server.views.ServerJFrameTextView;

public class Launcher extends WindowAdapter {
	
	private final JFrame frame;
	private Server server;

	public Launcher() {
		frame = new JFrame("Bomberman");
		frame.setLayout(new GridLayout(3, 1));
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(new NewServerAction(this));
		add(new NewSpectatorAction());
		add(new NewClientAction());
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private void add(Action action){
		frame.add(new JButton(action));
	}
	
	private void newServer(){
		server = new Server();
		new ServerJFrameTextView(server);
	}
	
	@Override
	public void windowClosing(WindowEvent e){
		if (server != null){
			server.stop();
		}
	}
	
	// Actions
	
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
			new SpectatorJFrameTextView(new Spectator());
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewClientAction extends AbstractAction {
		
		public NewClientAction(){
			super("New Client");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new ClientJFrameTextView(new PlayableClient());
		}
	}
	
	public static void main(String[] args){
		new Launcher();
	}
}
