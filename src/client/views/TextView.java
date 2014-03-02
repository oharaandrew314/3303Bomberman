package client.views;

import common.events.ViewUpdateEvent;

public class TextView extends View {
	
	@Override
	public void updateView(ViewUpdateEvent event){
		//clearTerminal();
		System.out.println(event.getGrid()); // TODO make nicer text view
	}
	
	private void clearTerminal(){
		try {
	        String os = System.getProperty("os.name");
	        
	        if (os.contains("Windows")) Runtime.getRuntime().exec("cls");
	        else Runtime.getRuntime().exec("clear");
	    }
	    catch (Exception e) {
	        System.out.print("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n"); //poor man's clear
	    }
	}
}
