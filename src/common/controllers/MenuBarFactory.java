package common.controllers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;

import client.views.View;

public class MenuBarFactory {
	
	public static JMenuBar createClientMenuBar(View view){
		JMenuBar menuBar = new JMenuBar();
		
		JMenu fileMenu = new JMenu();
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

}
