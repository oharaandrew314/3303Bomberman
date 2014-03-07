package common.views;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import server.controllers.Server;

public class MenuBarFactory {
	
	public static JMenuBar createClientMenuBar(View view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new ExitAction(view));
		
		menuBar.add(fileMenu);
		return menuBar;
	}
	
	public static JMenuBar createServerMenuBar(Server server, View view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.add(new LoadGameAction(server));
		fileMenu.add(new ExitAction(view));
		
		menuBar.add(fileMenu);
		return menuBar;
	}
	
	@SuppressWarnings("serial")
	private static class ExitAction extends AbstractAction {
		
		private final View view;
		
		public ExitAction(View view){
			super("Exit");
			this.view = view;
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			view.close();
		}
	}
	
	@SuppressWarnings("serial")
	private static class LoadGameAction extends AbstractAction {
		
		private final Server server;
		
		public LoadGameAction(Server server){
			super("Load Game");
			this.server = server;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new LevelLoaderView(server);
		}
	}

}
