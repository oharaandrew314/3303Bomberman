package client.controllers;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import server.controllers.Server;
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
	public void run(Server server){
		System.out.print("testing " + filename + "...");

		ArrayList<TestRunner> testClients = new ArrayList<TestRunner>();
		Thread[] threads = new Thread[events.size()];
		for(int i = 0 ; i != events.size();i++){
			testClients.add(new TestRunner(events.get(i)));
			threads[i] = new Thread(testClients.get(i));
		}
		boolean ready = false;
		long timeStartWaiting = System.currentTimeMillis();
		while(!ready){
			if(System.currentTimeMillis() - timeStartWaiting > 5000){
				System.out.println("connect: Waited too long... exiting");
				return;
			}
			boolean allConnected = true;
			for(TestRunner r : testClients){
				if(!r.isConnected()){
					allConnected = false;
				}
			}
			ready = allConnected;
		}
		if(!testClients.isEmpty()){
			testClients.get(0).sendGameStartEvent();
			timeStartWaiting = System.currentTimeMillis();
			boolean timeout = false;
			while(!testClients.get(0).isGameRunning() && !timeout){
				System.out.print("");
				timeout = System.currentTimeMillis() - timeStartWaiting > 1000;
			}
			//System.out.println(System.currentTimeMillis() - timeStartWaiting);
			if(timeout){
				System.err.println("timed out, game didn't start within the alloted time");
				return;
			}
		}
		
		for(TestRunner t : testClients){
			int index = testClients.indexOf(t);
			server.movePlayerTo(index+1, startLocations.get(index));
		}
		for(int i = 0 ; i != threads.length;i++){
			threads[i].start();
		}
		for(int i = 0 ; i != threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
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
					String[] lineSegment = str.split(";");
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
						String[] coordinates = locParts[1].split(",");
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