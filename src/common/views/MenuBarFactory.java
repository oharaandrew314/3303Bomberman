package common.views;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import server.controllers.Server;
import server.views.LevelGeneratorDialog;
import server.views.LevelLoaderDialog;

import common.models.Entity;
import common.models.Pillar;
import common.models.Wall;
import common.models.powerups.FlamePassPowerup;
import common.models.units.LineEnemy;
import common.models.units.PathFindingEnemy;
import common.models.units.RandomEnemy;

public class MenuBarFactory {
	
	// Factories
	
	public static JMenuBar createClientMenuBar(AbstractView view){
		MenuBuilder builder = new MenuBuilder(view);
		builder.addToHelp(new ControlsAction(view.getComponent()));
		return builder.bar;
	}
	
	public static JMenuBar createServerMenuBar(Server server, AbstractView view){
		MenuBuilder builder = new MenuBuilder(view);
		builder.addToFile(new LoadGridAction(view.getComponent(), server));
		builder.addToFile(new GenerateGridAction(view.getComponent(), server));
		builder.addToFile(new EndGameAction(server));
		return builder.bar;
	}
	
	public static JMenuBar createSpectatorMenuBar(AbstractView view){
		return new MenuBuilder(view).bar;
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
			String helpText = String.format(
				"%s: Pillar | %s: Wall\n" + 
				"%s: Random Enemy | %s: Line enemy | %s: Smart Enemy\n" + 
				"#: Player | B: Bomb | %s: Powerup",
				getEntityChar(Pillar.class),
				getEntityChar(Wall.class),
				getEntityChar(RandomEnemy.class),
				getEntityChar(LineEnemy.class),
				getEntityChar(PathFindingEnemy.class),
				getEntityChar(FlamePassPowerup.class)
			);
			JOptionPane.showMessageDialog(parent, helpText);
			
		}
		
		private String getEntityChar(Class<? extends Entity> type){
			try {
				return type.newInstance().toString();
			} catch (InstantiationException | IllegalAccessException e) {
				return "<Error>";
			}
		}		
	}
	
	private static class MenuBuilder {
		
		public final JMenuBar bar;
		private final JMenu fileMenu, helpMenu;
		
		public MenuBuilder(AbstractView view){
			bar = new JMenuBar();
			
			bar.add(fileMenu = new JMenu("File"));
			bar.add(helpMenu = new JMenu("Help"));
			
			addToFile(new ExitAction(view));
			addToHelp(new GridLegendAction(view.getComponent()));
		}
		
		public void addToFile(Action action){
			fileMenu.add(action);
		}
		
		public void addToHelp(Action action){
			helpMenu.add(action);
		}
	}
}
