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
	
	public static JMenuBar createClientMenuBar(AbstractView view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new ExitAction(view));
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new ControlsAction(view.getComponent()));
		helpMenu.add(new GridLegendAction(view.getComponent()));
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}
	
	public static JMenuBar createServerMenuBar(Server server, AbstractView view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new LoadGridAction(view.getComponent(), server));
		fileMenu.add(new GenerateGridAction(view.getComponent(), server));
		fileMenu.add(new EndGameAction(server));
		fileMenu.add(new ExitAction(view));
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new GridLegendAction(view.getComponent()));
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}
	
	public static JMenuBar createSpectatorMenuBar(AbstractView view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new ExitAction(view));
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.add(new GridLegendAction(view.getComponent()));
		
		menuBar.add(fileMenu);
		menuBar.add(helpMenu);
		return menuBar;
	}
	
	// Actions
	
	@SuppressWarnings("serial")
	private abstract static class ServerAction extends AbstractAction {
		
		protected final Server server;
		protected final Component parent;
		
		protected ServerAction(String name, Server server, Component parent){
			super(name);
			this.server = server;
			this.parent = parent;
		}
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
	private static class LoadGridAction extends ServerAction {
		
		public LoadGridAction(Component parent, Server server){
			super("Load Grid", server, parent);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new LevelLoaderDialog(parent, server);
		}
	}
	
	@SuppressWarnings("serial")
	private static class GenerateGridAction extends ServerAction {
		
		public GenerateGridAction(Component parent, Server server){
			super("Generate Grid", server, parent);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new LevelGeneratorDialog(parent, server);
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
				"Movement: UpDownLeftRight, Bomb: Space\n\n" +
				"Universal:\n" +
				"Start game: Enter | Disconnect: Escape"
			);
			JOptionPane.showMessageDialog(parent, controls);
		}
	}
	
	@SuppressWarnings("serial")
	private static class EndGameAction extends ServerAction {
		
		public EndGameAction(Server server){
			super("End Game", server, null);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			server.endGame(null);
		}
	}
	
	@SuppressWarnings("serial")
	private static class GridLegendAction extends AbstractAction {
		
		private final Component parent;
		
		public GridLegendAction(Component parent){
			super("Grid Legend");
			this.parent = parent;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			String helpText = (
				"X: Pillar | *: Wall\n" + 
				"R: Random Enemy | L: Line enemy | S: Smart Enemy\n" + 
				"[1-9]: player | B: Bomb | P: Powerup"
			);
			JOptionPane.showMessageDialog(parent, helpText);
			
		}
		
	}
}
