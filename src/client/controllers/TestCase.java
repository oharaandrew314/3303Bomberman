package client.controllers;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.awt.event.KeyEvent.*;


public class TestCase {
	public static final String UP = "UP";
	public static final String DOWN = "DOWN";
	public static final String RIGHT = "RIGHT";
	public static final String LEFT = "LEFT";
	private static final String TEST_PATH = "testFiles/";
	private ArrayList<ArrayList<Integer>> events;
	private ArrayList<Point> startLocations;
	private String filename;
	
	public TestCase(String filename){
		
		startLocations = new ArrayList<Point>();
		events = readEvents(filename);
		this.filename = filename;
		
	}
	
	/**
	 * runs the testCase
	 * Creates a thread for each player involved with the testCase
	 * Each thread is a player with all the events that player performs
	 */
	public void run(){
		System.out.print("testing " + filename + "...");
		
		Thread[] threads = new Thread[events.size()];
		for(int i = 0 ; i != events.size();i++){
			if(startLocations.get(i) != null){
				threads[i] = new Thread(new TestRunner(events.get(i), startLocations.get(i)));
			} else{
				threads[i] = new Thread(new TestRunner(events.get(i)));
			}
			threads[i].start();
		}
		for(Thread t: threads){
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Done");
	}
	
	
	/**
	 * Parses the file, separating each players events
	 * @param filename
	 * @return List of Player events for each player (list of lists)
	 */
	private ArrayList<ArrayList<Integer>> readEvents(String filename) {
		ArrayList<ArrayList<Integer>> events = new ArrayList<ArrayList<Integer>>();
		String str = "";
		
		try{
			InputStream in = this.getClass().getClassLoader().getResourceAsStream(TEST_PATH + filename + ".txt");
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
		    if (in!=null) {                         
		    	
		    	
		    	//Get the number of players in the testCase from the first line
				if((str = bufferedReader.readLine()) != null) {
					String[] lineSegment = str.split(",");
					String[] player = lineSegment[0].split("=");
					int players = Integer.parseInt(player[1].trim());
					//Location=[ 1-> 0,0 : 2->3,0]
					String loc = lineSegment[1].trim().split("=")[1];
					String[] locSplit = loc.split(":");
					for(int i = 0 ; i != players ; i++){
						events.add(new ArrayList<Integer>());
						startLocations.add(null);
					}
					for(int i = 0 ; i != locSplit.length ; i++){
						String[] locParts = locSplit[i].replaceAll("\\[", "").replaceAll("\\]", "").split("->");
						int id = Integer.parseInt(locParts[0].trim());
						String[] coordinates = locParts[1].split(";");
						Point point = new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
						startLocations.set(id-1, point);
					}
					
						
				}
				
				//Get each command and all details associated with it
		        while ((str = bufferedReader.readLine()) != null) { 
		        	String[] sections = str.split(":");
		        	int player = Integer.parseInt(sections[0].trim());
		        	String command = sections[1].trim();
		        	
		        		switch(command){
			        		case UP:  events.get(player-1).add(VK_UP);
		                    	break;
			        		case DOWN:  events.get(player-1).add(VK_DOWN);
		                		break;
			        		case LEFT:  events.get(player-1).add(VK_LEFT);
			        			break;
			        		case RIGHT:  events.get(player-1).add(VK_RIGHT);
		                		break;
		                	default:
		                		break;
		        		}
		        	
		        }               
		    }
		}
		catch (NumberFormatException | IOException e) {
			e.printStackTrace();
		}
		return events;
	}
	
}

