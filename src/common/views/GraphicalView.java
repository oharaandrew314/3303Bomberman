package common.views;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.TextArea;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import client.controllers.PlayableClient;
import client.controllers.Spectator;
import client.views.ClientTextGenerator;
import client.views.SpectatorTextGenerator;
import common.content.ImageLoader;
import common.controllers.GameController;
import common.models.Entity;
import common.models.Grid;

public class GraphicalView extends AbstractView{
	
	private GraphicsPanel graphicsPanel;
	private TextArea console;

	public GraphicalView(GameController gc, TextGenerator textGen) {
		super(gc, textGen);
	}
	
	@Override
	protected void initComponents() {
		// Add graphics panel
		frame.add(graphicsPanel = new GraphicsPanel(), BorderLayout.CENTER);
		
		// Add Console
		frame.add(console = new TextArea(), BorderLayout.SOUTH);
		console.setEditable(false);
	}

	@Override
	public void displayMessage(String message) {
		console.append(message + LINE_SEP);
	}
	
	@Override
	public void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		graphicsPanel.addKeyListener(l);
		console.addKeyListener(l);
	}

	@Override
	public void displayGrid(Grid grid) {
		graphicsPanel.repaint(grid);
	}
	
	@SuppressWarnings("serial")
	private class GraphicsPanel extends JPanel {
		
		private Grid nextGrid;
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			if (nextGrid != null){
				for (Point point : nextGrid.keySet()){
					Entity e = nextGrid.getVisibleEntity(point);
					if (e != null){
						BufferedImage img = ImageLoader.getImage(e);
						g.drawImage(
							img,
							point.x * GRID_TEXT_SIZE, point.y * GRID_TEXT_SIZE,
							GRID_TEXT_SIZE, GRID_TEXT_SIZE,
							this
						);
					}
				}
			}
			nextGrid = null;
		}
		
		public void repaint(Grid grid){
			nextGrid = grid;
			repaint();
		}
	}
	
	// Factory methods
	
	public static AbstractView newClientView(){
		PlayableClient client = new PlayableClient();
		AbstractView view = new GraphicalView(
			client, new ClientTextGenerator()
		);
		view.addKeyListener(client);
		view.addMenuBar(MenuBarFactory.createClientMenuBar(view));
		return view;
	}
	
	public static AbstractView newSpectatorView(){
		AbstractView view = new GraphicalView(
			new Spectator(), new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createSpectatorMenuBar(view));
		return view;
	}
}
