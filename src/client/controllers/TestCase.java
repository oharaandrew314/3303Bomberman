package client.controllers;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import common.controllers.NetworkController;
import static java.awt.event.KeyEvent.*;


public class TestCase {
	private Collection<ArrayList<Integer>> events;
	private Collection<Point> playerLocations;
	private NetworkController[] networkControllers;
	private String filename;
	
	public TestCase(String filename, NetworkController[] networkControllers) throws IOException{
		this.networkControllers = networkControllers;
		playerLocations = new ArrayList<Point>();
		events = readEvents(filename);
		this.filename = filename;
		
	}
	
	/**
	 * runs the testCase
	 * Creates a thread for each player involved with the testCase
	 * Each thread is a player with all the events that player performs
	 */
	public void run(){
		System.out.println(filename + ":");
		Thread[] threads = new Thread[events.size()];
		for(int i = 0 ; i != events.size();i++){
				threads[i] = new TestRunner(networkControllers[i], ((ArrayList<ArrayList<Integer>>) events).get(i) , i+1);
				threads[i].start();
		}
		for(Thread t: threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	/**
	 * Parses the file, separating each players events
	 * @param filename
	 * @return List of Player events for each player (list of lists)
	 */
	private ArrayList<ArrayList<Integer>> readEvents(String filename) {
		ArrayList<ArrayList<Integer>> events = new ArrayList<ArrayList<Integer>>();
		//read file
		String str = "";
		
		try{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream("testFiles/" + filename + ".txt");
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		    if (in!=null) {                         
		    	
				if((str = bufferedReader.readLine()) != null) {
					String[] temp = str.split("=");
					int players = Integer.parseInt(temp[1].trim());
					for(int i = 0 ; i != players ; i++){
						events.add(new ArrayList<Integer>());
					}
						//setUp stuff here maybe
				}
				
		        while ((str = bufferedReader.readLine()) != null) { 
		        	int player = Integer.parseInt(str.substring(0, 2).trim());
		        	String newString = str.substring(2).trim();
		        	//if(newString.contains("Coordinates"))
		        	//	playerLocations.add(parseCoordinates(player, newString));
		        	//else{
		        		switch(newString){
			        		case "UP":  events.get(player-1).add(VK_UP);
		                    	break;
			        		case "DOWN":  events.get(player-1).add(VK_DOWN);
		                		break;
			        		case "LEFT":  events.get(player-1).add(VK_LEFT);
			        			break;
			        		case "RIGHT":  events.get(player-1).add(VK_RIGHT);
		                		break;
		                	default:
		                		break;
		        		}
		        	
		        }               
		    }
		}
		catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return events;
	}
	
	/**
	 * 
	 * @param player
	 * @param str
	 * @return
	 */
	//private Point parseCoordinates(int player, String str){
	//	String coordinates = str.split("=")[1];
	//	String[] temp = coordinates.split(",");
	//	Point playerLocation = new Point(Integer.parseInt(temp[0].trim()), Integer.parseInt(temp[1].trim()));
	//	return playerLocation;
	//}
}
