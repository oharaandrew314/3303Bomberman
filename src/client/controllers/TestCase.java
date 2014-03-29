package client.controllers;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_SPACE;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import server.controllers.Server;

import common.controllers.GameController.GridBuffer;
import common.models.units.Player;


public class TestCase {
	public static final String UP = "UP";
	public static final String DOWN = "DOWN";
	public static final String RIGHT = "RIGHT";
	public static final String LEFT = "LEFT";
	public static final String SPACE = "SPACE";
	private static final String TEST_PATH = "testFiles/";
	private ArrayList<ArrayList<Integer>> events;
	private ArrayList<ArrayList<Long>> timings;
	private ArrayList<Long> latencies;
	private ArrayList<Point> startLocations;
	private String filename;
	private String gridFileName;

	public TestCase(String filename){

		startLocations = new ArrayList<Point>();
		timings = new ArrayList<ArrayList<Long>>();
		latencies = new ArrayList<Long>();
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
		
		//create the threads
		for(int i = 0 ; i != events.size();i++){
			testClients.add(new TestRunner(events.get(i), timings.get(i)));
			threads[i] = new Thread(testClients.get(i));
		}
		
		//Wait for everyone to connect
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
		
		//Start the game
		if(!testClients.isEmpty()){
			testClients.get(0).sendGameStartEvent();
			timeStartWaiting = System.currentTimeMillis();
			
			boolean startedForEveryone = false;
			while(!startedForEveryone){
				if(System.currentTimeMillis() - timeStartWaiting > 1000){
					System.out.println("gameStart: Waited too long... exiting");
					return;
				}
				boolean allStarted = true;
				for(TestRunner r : testClients){
					if(!r.isGameRunning()){
						allStarted = false;
					}
				}
				startedForEveryone = allStarted;
			}
		}
		
		//move the players to their start locations
		for(TestRunner t : testClients){
			int index = testClients.indexOf(t);
			
			Player player = server.getPlayer(index+1);
			try(GridBuffer buf = server.acquireGrid()){
				if (buf.grid.contains(player)){
					buf.grid.remove(player);
				}
				buf.grid.set(player, startLocations.get(index));
			}
			
		}
		
		//start the threads
		for(int i = 0 ; i != threads.length;i++){
			threads[i].start();
		}
		
		//wait for threads to finish - needs to be a seperate loop from starting the threads
		//otherwise they will run one at a time
		for(int i = 0 ; i != threads.length;i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
		//disconnect all the testClients
		for(TestRunner t : testClients){
			t.stop();
			latencies.addAll(t.getLatencyList());
			
		}
		long sum = 0;
		for(long l : latencies){
			sum += l;
		}
		System.out.println("Average Latency: " + sum/latencies.size());
		System.out.println("Done");
	}
	
	private void setGridFileName(String gridFileName){
		this.gridFileName = gridFileName;
	}
	
	public String getGridFileName(){
		return "test/" + gridFileName+".json";
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
						timings.add(new ArrayList<Long>());
						startLocations.add(null);
					}
					for(int i = 0 ; i != locSplit.length ; i++){
						String[] locParts = locSplit[i].replaceAll("\\[", "").replaceAll("\\]", "").split("->");
						int id = Integer.parseInt(locParts[0].trim());
						String[] coordinates = locParts[1].split(",");
						Point point = new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1]));
						startLocations.set(id-1, point);
					}
					
					String gridFile = lineSegment[2].trim().split("=")[1].trim();
					setGridFileName(gridFile);

				}

				//Get each command and all details associated with it
		        while ((str = bufferedReader.readLine()) != null) { 
		        	String[] sections = str.split(":");
		        	int player = Integer.parseInt(sections[0].trim());
		        	String command = sections[1].trim();
		        	
		        	//get and set the timing between events
		        	if(sections.length == 3){
		        		String timing = sections[2].trim();
		        		timings.get(player-1).add(Long.parseLong(timing));
		        	} else{
		        		timings.get(player-1).add(TestRunner.DEFAULT_WAIT_BETWEEN_ACTIONS);
		        	}

		        	switch(command){
			        	case UP:  events.get(player-1).add(VK_UP);
		                   	break;
			        	case DOWN:  events.get(player-1).add(VK_DOWN);
		                	break;
			        	case LEFT:  events.get(player-1).add(VK_LEFT);
			        		break;
			        	case RIGHT:  events.get(player-1).add(VK_RIGHT);
		                	break;
			        	case SPACE: events.get(player-1).add(VK_SPACE);
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
	
	public ArrayList<Long> getTestCaseLatencies(){
		return latencies;
	}
	

}