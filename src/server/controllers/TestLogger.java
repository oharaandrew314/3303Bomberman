package server.controllers;

import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import common.events.Event;
import common.events.GameKeyEvent;
import common.events.GameStartEvent;
import common.events.ViewUpdateEvent;
import common.models.Grid;

public class TestLogger implements Observer{
	
	private static final String LINE_SEP = System.getProperty("line.separator");
	private FileWriter writer;

	public TestLogger() {
		try {
			writer = new FileWriter(new File("bomberman.log"), true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String getPlayerText(Event event){
		return "Player " + event.getPlayerID() + " - ";
	}

	@Override
	public void update(Observable o, Object arg) {
		String output = null;
		
		if (arg instanceof GameKeyEvent){
			GameKeyEvent event = (GameKeyEvent) arg;
			output = getPlayerText(event) + KeyEvent.getKeyText(event.getKeyCode());
		}
		
		else if (arg instanceof ViewUpdateEvent){
			Grid grid = ((ViewUpdateEvent)arg).getGrid();
			if (grid != null){
				output = grid.toString();
			}
		}
		
		else if (arg instanceof GameStartEvent){
			String bar =  LINE_SEP + "====================" + LINE_SEP;
			output = LINE_SEP + LINE_SEP + bar + "Game Start!" + bar;
		}
		
		else if (arg instanceof Event){
			Event event = (Event) arg;
			output = getPlayerText(event) + event.getClass().getSimpleName();
		}
		
		if (output != null){
			try {
				writer.write(output + LINE_SEP);
				writer.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
