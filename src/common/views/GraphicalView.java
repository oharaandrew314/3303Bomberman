package common.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyListener;
import java.net.InetSocketAddress;

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

	public GraphicalView(GameController gc, TextGenerator textGen) {
		super(gc, textGen);
	}
	
	@Override
	protected void initComponents() {
		// Add graphics panel
		frame.add(graphicsPanel = new GraphicsPanel(), BorderLayout.CENTER);
		
		// Enable Console
		setConsoleEnabled(true);
	}
	
	@Override
	public void addKeyListener(KeyListener l) {
		super.addKeyListener(l);
		graphicsPanel.addKeyListener(l);
	}

	@Override
	public void displayGrid(Grid grid) {
		graphicsPanel.repaint(grid);
	}
	
	@SuppressWarnings("serial")
	private class GraphicsPanel extends JPanel {
		
		private final Color BG_COLOR = new Color(201, 193,  97);
		private Grid nextGrid;
		
		@Override
		public void paintComponent(Graphics g){
			super.paintComponent(g);
			
			if (nextGrid != null){
				// Paint grid area background
				g.setColor(BG_COLOR);
				g.fillRect(
					0, 0,
					nextGrid.getSize().width * GRID_SQUARE_SIZE,
					nextGrid.getSize().height * GRID_SQUARE_SIZE
				);
				
				// Paint entities
				for (Point point : nextGrid.keySet()){
					Entity e = nextGrid.getVisibleEntity(point);
					if (e != null){
						g.drawImage(
							ImageLoader.getImage(e),
							point.x * GRID_SQUARE_SIZE, point.y * GRID_SQUARE_SIZE,
							GRID_SQUARE_SIZE, GRID_SQUARE_SIZE,
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
	
	public static AbstractView newClientView(InetSocketAddress address){
		PlayableClient client = new PlayableClient(address);
		AbstractView view = new GraphicalView(
			client, new ClientTextGenerator()
		);
		view.addKeyListener(client);
		view.addMenuBar(MenuBarFactory.createClientMenuBar(view));
		return view;
	}
	
	public static AbstractView newSpectatorView(InetSocketAddress address){
		AbstractView view = new GraphicalView(
			new Spectator(address),
			new SpectatorTextGenerator()
		);
		view.addMenuBar(MenuBarFactory.createSpectatorMenuBar(view));
		return view;
	}
}
