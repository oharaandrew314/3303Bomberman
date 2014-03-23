package common;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;

import client.controllers.TestDriver;
import common.views.GraphicalView;
import common.views.TextView;
import server.controllers.Server;

public class Launcher extends WindowAdapter {
	
	private final JFrame frame;
	private Server server;
	
	private final Collection<JButton> clientButtons;

	public Launcher() {
		frame = new JFrame("Bomberman");
		frame.setLayout(new GridLayout(4, 1));
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		clientButtons = new ArrayList<>();
		
		add(new NewServerAction(this));
		addClientButton(new NewSpectatorAction());
		addClientButton(new NewClientAction());
		addClientButton(new TestDriverAction(this));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private JButton addClientButton(Action action){
		JButton button = add(action);
		clientButtons.add(button);
		return button;
	}
	
	private JButton add(Action action){
		JButton button = new JButton(action);
		frame.add(button);
		return button;
	}
	
	private void newServer(){
		TextView.newServerView(server = new Server());
		for (JButton clientButton : clientButtons){
			clientButton.setEnabled(true);
		}
	}
	
	@Override
	public void windowClosing(WindowEvent e){
		if (server != null){
			server.stop();
		}
	}
	
	public Server getServer(){
		return server;
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
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GraphicalView.newSpectatorView();
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewClientAction extends AbstractAction {
		
		public NewClientAction(){
			super("New Client");
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			GraphicalView.newClientView();
		}
	}
	
	public static void main(String[] args){
		new Launcher();
	}
	
	@SuppressWarnings("serial")
	private static class TestDriverAction extends AbstractAction {
		
		private final Launcher launcher;
		
		public TestDriverAction(Launcher launcher){
			super("Run Test Driver");
			this.launcher = launcher;
			setEnabled(false);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TestDriver.run(launcher.getServer());
			
		}
	}
}
