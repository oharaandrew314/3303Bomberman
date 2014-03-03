import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import server.content.GridLoader;
import server.controllers.Server;
import test.integration.TestDriver;
import client.controllers.Spectator;
import client.views.JFrameTextView;


public class DemoRunner {
	
	private final JFrame frame;
	private Server server;

	public DemoRunner() {
		frame = new JFrame();
		frame.setLayout(new GridLayout(3, 2));

		add(new ServerAction());
		add(new SpectatorAction());
		add(new TestDriverAction());
		add(new ResetServerAction());
		
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	
	public boolean hasServer(){
		return server != null;
	}
	
	public void resetServer(){
		if (hasServer()){
			server.reset();
		}
	}
	
	public void message(String message){
		JOptionPane.showMessageDialog(frame, message);
	}
	
	// Actions
	
	private void add(AbstractAction action){
		frame.add(new JButton(action));
	}
	
	@SuppressWarnings("serial")
	private class ServerAction extends AbstractAction {
		
		public ServerAction(){
			super("Server");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			server = new Server();
			server.newGame(GridLoader.loadGrid("grid1.json"));
		}
		
	}
	
	@SuppressWarnings("serial")
	private class SpectatorAction extends AbstractAction {
		
		public SpectatorAction(){
			super("Spectator");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (hasServer()){
				new Spectator(new JFrameTextView());
			} else {
				message("Cannot start spectator without a server");
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class TestDriverAction extends AbstractAction {
		
		public TestDriverAction(){
			super("Test Driver");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (hasServer()){
				TestDriver.run(server);
			} else {
				message("Cannot start test driver without a server");
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class ResetServerAction extends AbstractAction {
		
		public ResetServerAction(){
			super("Reset Server");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (hasServer()){
				resetServer();
			} else {
				message("Cannot reset server without a server");
			}
				
		}
	}
	
	public static void main(String[] args){
		new DemoRunner();
	}

}
