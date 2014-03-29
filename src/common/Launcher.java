package common;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.InetSocketAddress;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import server.controllers.Server;
import client.controllers.TestDriver;
import common.controllers.NetworkController;
import common.views.GraphicalView;
import common.views.TextView;

public class Launcher extends WindowAdapter {
	
	private final JFrame frame;
	private Server server;

	public Launcher() {
		frame = new JFrame("Bomberman");
		frame.setLayout(new GridLayout(4, 1));
		frame.addWindowListener(this);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		add(new NewServerAction(this));
		add(new NewSpectatorAction(this));
		add(new NewClientAction(this));
		add(new TestDriverAction(this));
		
		frame.pack();
		frame.setVisible(true);
	}
	
	private JButton add(Action action){
		JButton button = new JButton(action);
		frame.add(button);
		return button;
	}
	
	private void newServer(){
		TextView.newServerView(server = new Server());
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
	private static abstract class LauncherAction extends AbstractAction {
		
		protected final Launcher launcher;
		
		public LauncherAction(Launcher launcher, String name){
			super(name);
			this.launcher = launcher;
		}
		
		protected InetSocketAddress getAddress(){
			String address = JOptionPane.showInputDialog(
				launcher.frame, 
				"Please enter server connectionString. <ip>:<port>\n" +
				"e.g. 192.168.1.2:4200",
				String.format("%s:%s", NetworkController.LOCALHOST, NetworkController.SERVER_PORT)
			);
			if (address != null && address.contains(":") && address.split(":").length == 2) {
				String[] parts = address.split(":");
				return new InetSocketAddress(parts[0], Integer.parseInt(parts[1]));
			} else if (address != null && !address.contains(":")){
				return new InetSocketAddress(address, NetworkController.SERVER_PORT);
			} else {
				return null;
			}
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewServerAction extends LauncherAction {
		
		public NewServerAction(Launcher launcher){
			super(launcher, "New Server");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			launcher.newServer();
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewSpectatorAction extends LauncherAction {
		
		public NewSpectatorAction(Launcher launcher){
			super(launcher, "New Spectator");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			InetSocketAddress address = getAddress();
			if (address != null){
				GraphicalView.newSpectatorView(address);
			} else {
				JOptionPane.showMessageDialog(launcher.frame, "Invalid address");
			}
			
		}
	}
	
	@SuppressWarnings("serial")
	private static class NewClientAction extends LauncherAction {
		
		public NewClientAction(Launcher launcher){
			super(launcher, "New Client");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			InetSocketAddress address = getAddress();
			if (address != null){
				GraphicalView.newClientView(address);
			} else {
				JOptionPane.showMessageDialog(launcher.frame, "Invalid address");
			}
		}
	}
	
	public static void main(String[] args){
		new Launcher();
	}
	
	@SuppressWarnings("serial")
	private static class TestDriverAction extends LauncherAction {
		
		public TestDriverAction(Launcher launcher){
			super(launcher, "Run Test Driver");
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			TestDriver.run(launcher.getServer());
			
		}
	}
}
