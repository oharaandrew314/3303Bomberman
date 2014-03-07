package server.views;

import javax.swing.JFrame;

import server.controllers.Server;
import client.views.SpectatorJFrameTextView;

import common.views.MenuBarFactory;

public class ServerJFrameTextView extends SpectatorJFrameTextView {

	public ServerJFrameTextView(Server server) {
		super(server);
		addJMenuBar(MenuBarFactory.createServerMenuBar(server, this));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
