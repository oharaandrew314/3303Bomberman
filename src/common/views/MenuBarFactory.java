package common.views;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import server.controllers.Server;
import server.views.LevelGeneratorDialog;
import server.views.LevelLoaderDialog;

public class MenuBarFactory {
	
	public static JMenuBar createClientMenuBar(JFrameTextView view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new ExitAction(view));
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new ControlsAction(view.asComponent()));
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}
	
	public static JMenuBar createServerMenuBar(Server server, JFrameTextView view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new LoadGridAction(server));
		fileMenu.add(new GenerateGridAction(server));
		fileMenu.add(new ExitAction(view));
		
		menuBar.add(fileMenu);
		return menuBar;
	}
	
	public static JMenuBar createSpectatorMenuBar(JFrameTextView view){
JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new ExitAction(view));
		
		menuBar.add(fileMenu);
		return menuBar;
	}
	
	@SuppressWarnings("serial")
	private static class ExitAction extends AbstractAction {
		
		private final AbstractView view;
		
		public ExitAction(AbstractView view){
			super("Exit");
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			view.close();
		}
	}
	
	@SuppressWarnings("serial")
	private static class LoadGridAction extends AbstractAction {
		
		private final Server server;
		
		public LoadGridAction(Server server){
			super("Load Grid");
			this.server = server;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new LevelLoaderDialog(server);
		}
	}
	
	@SuppressWarnings("serial")
	private static class GenerateGridAction extends AbstractAction {
		
		private final Server server;
		
		public GenerateGridAction(Server server){
			super("Generate Grid");
			this.server = server;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new LevelGeneratorDialog(server);
		}
	}
	
	@SuppressWarnings("serial")
	private static class ControlsAction extends AbstractAction {
		
		private final Component parent;
		
		public ControlsAction(Component parent){
			super("Controls");
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String controls = (
				"Righty Controls:\n" +
				"Movement: WASD, Bomb: F\n\n" +
				"Southpaw Controls:\n" +
				"Movement: IJKL, Bomb: ;\n\n" +
				"n00b Controls:\n" +
				"Movement: UpDownLeftRight, Bomb: Space"
			);
			JOptionPane.showMessageDialog(parent, controls);
		}
	}
}
