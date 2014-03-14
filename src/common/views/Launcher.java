package common.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import server.controllers.Server;

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
		JFrameTextView.newServerView(server = new Server());		
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
			setEnabled(false);
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewSpectatorAction extends AbstractAction {
		
		public NewSpectatorAction(){
			super("New Spectator");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFrameTextView.newSpectatorView();
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewClientAction extends AbstractAction {
		
		public NewClientAction(){
			super("New Client");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			JFrameTextView.newClientView();
		}
	}
	
	public static void main(String[] args){
		new Launcher();
	}
}
