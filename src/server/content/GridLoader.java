package server.content;

import java.awt.Dimension;
import java.awt.Point;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;

import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.RandomEnemy;
import common.models.Wall;

public class GridLoader {
	
	private static final String GRID_PATH = "grids/";
	private final Gson gson;
	private final ClassLoader loader;
	
	public GridLoader(){
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Grid.class, new GridDeserializer());
		gson = builder.serializeNulls().create();

		loader = getClass().getClassLoader();
	}
	
	public Grid load(String gridFileName) throws CreateGridException {
		InputStream s = loader.getResourceAsStream(GRID_PATH + gridFileName);
		
		Reader reader = null;
		try {
			reader = new InputStreamReader(s);
		} catch (NullPointerException ex){
			throw new CreateGridException(
				"Error loading grid: " + gridFileName + " does not exist"
			);
		}
		
		try{
			return gson.fromJson(reader, Grid.class);
		} catch (JsonSyntaxException ex){
			throw new CreateGridException("Error loading grid: Invalid content.");
		} 
	}
	
	public static Grid loadGrid(String gridFileName) throws CreateGridException {
		GridLoader loader = new GridLoader();
		return loader.load(gridFileName);
	}
	
	private class GridDeserializer implements JsonDeserializer<Grid>{
		
		private int playerIndex;
		
		@Override
		public Grid deserialize(
			JsonElement ele, Type arg1, JsonDeserializationContext arg2
		) throws JsonParseException {		
			playerIndex = 1;
			
			JsonObject gridObj = ele.getAsJsonObject();
			
			// Create grid
			JsonObject sizeObj = gridObj.get("size").getAsJsonObject();
			Grid grid = new Grid(
				new Dimension(
					sizeObj.get("width").getAsInt(),
					sizeObj.get("height").getAsInt()
				)
			);
			
			JsonArray squares = gridObj.get("squares").getAsJsonArray();
			for (JsonElement squareEle: squares){
				if (!squareEle.isJsonNull()){
					addSquare(grid, squareEle);
				}
			}
			
			return grid;
		}
		
		private void addSquare(Grid grid, JsonElement squareEle){
			JsonObject squareObj = squareEle.getAsJsonObject();
			
			// Get Point
			JsonObject pointObj = squareObj.get("point").getAsJsonObject();
			Point point = new Point(
				pointObj.get("x").getAsInt(),
				pointObj.get("y").getAsInt()
			);
			
			//Load entity and add to grid
			for (JsonElement entityEle : squareObj.get("entities").getAsJsonArray()){
				if (!entityEle.isJsonNull()){
					grid.set(loadEntity(entityEle), point);
				}
			}
		}
		
		private Entity loadEntity(JsonElement entityEle){
			String name = entityEle.getAsString();
			
			switch(name.toLowerCase()){
				case "pillar": return new Pillar();
				case "wall": return new Wall();
				case "player": return new Player(playerIndex++);
				case "door": return new Door();
				case "randomenemy": return new RandomEnemy();
				default: throw new IllegalArgumentException(
					name + " is an illegal entity"
				);
			}
		}
	}
}
