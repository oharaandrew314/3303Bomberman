package server.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import common.models.Grid;
import server.content.GridGenerator;
import server.controllers.Server;

@SuppressWarnings("serial")
public class LevelGeneratorDialog extends JDialog {
	
	private final JTextField width, height, seed;
	private final Server server;

	public LevelGeneratorDialog(Component parent, Server server) {
		this.setLocationRelativeTo(parent);
		this.server = server;
		setLayout(new GridLayout(5, 2));
		
		add(new JLabel("Width:"));
		add(new JLabel("Height:"));
		
		add(width = new JTextField());
		add(height = new JTextField());
		
		add(new JLabel("Seed:"));
		add(seed = new JTextField());
		
		JButton submitButton = new JButton(new GenerateAction(this));
		getRootPane().setDefaultButton(submitButton);
		add(submitButton);
		
		pack();
		setVisible(true);
	}
	
	private void generateGrid(){
		Dimension size = new Dimension(
			Integer.parseInt(width.getText()),
			Integer.parseInt(height.getText())
		);
		
		Grid grid = null;
		if (seed.getText().length() == 0){
			grid = GridGenerator.createRandomGrid(size);
		} else {
			grid = GridGenerator.createRandomGrid(
				size, Integer.parseInt(seed.getText())
			);
		}

		server.newGame(grid);
	}
	
	private static class GenerateAction extends AbstractAction {
		
		private final LevelGeneratorDialog dialog;
		
		public GenerateAction(LevelGeneratorDialog dialog){
			super("Generate");
			this.dialog = dialog;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			dialog.generateGrid();	
			dialog.setVisible(false);
			dialog.dispose();
		}
	}
}
