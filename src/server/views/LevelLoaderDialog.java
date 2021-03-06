package server.views;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import server.content.CreateGridException;
import server.content.GridLoader;
import server.controllers.Server;

@SuppressWarnings("serial")
public class LevelLoaderDialog extends JDialog {
	
	private final Server server;
	private final JTextField gridName;

	public LevelLoaderDialog(Component parent, Server server) {
		this.setLocationRelativeTo(parent);
		this.server = server;
		
		setLayout(new GridLayout(3, 1));
		add(new JLabel("Enter the grid to load"));
		add(gridName = new JTextField());
		
		JButton submitButton = new JButton(new LoadNewGameAction(this));
		getRootPane().setDefaultButton(submitButton);
		add(submitButton);
		
		pack();
		setVisible(true);
	}
	
	private void loadNewGame(){
		try {
			server.newGame(GridLoader.loadGrid(gridName.getText()));
		} catch (CreateGridException e) {
			JOptionPane.showMessageDialog(this, "Error loading grid");
		}
	}
	
	private static class LoadNewGameAction extends AbstractAction {
		
		private final LevelLoaderDialog loader;
		
		public LoadNewGameAction(LevelLoaderDialog loader){
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
