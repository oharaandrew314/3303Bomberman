package common.views;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import server.content.GridLoader;
import server.controllers.Server;

@SuppressWarnings("serial")
public class LevelLoaderView extends JDialog {
	
	private final Server server;
	private final JTextField gridName;

	public LevelLoaderView(Server server) {
		this.server = server;
		
		setLayout(new GridLayout(3, 1));
		add(new JLabel("Enter the grid to load"));
		add(gridName = new JTextField());
		add(new JButton(new LoadNewGameAction(this)));
		
		pack();
		setVisible(true);
	}
	
	private void loadNewGame(){
		server.newGame(GridLoader.loadGrid(gridName.getText()));
	}
	
	private static class LoadNewGameAction extends AbstractAction {
		
		private final LevelLoaderView loader;
		
		public LoadNewGameAction(LevelLoaderView loader){
			super("Load Game");
			this.loader = loader;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			loader.loadNewGame();
			loader.setVisible(false);
			loader.dispose();
		}
	}

}
