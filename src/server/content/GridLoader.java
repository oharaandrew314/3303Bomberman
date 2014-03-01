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
import common.models.Entity;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.Wall;

public class GridLoader implements JsonDeserializer<Grid>{
	
	private static final String GRID_PATH = "grids/";
	private static Gson gson;
	
	public static Grid loadGrid(String gridFileName){
		if (gson == null){
			initialize();
		}
		
		ClassLoader loader = GridLoader.class.getClassLoader();
		InputStream s = loader.getResourceAsStream(GRID_PATH + gridFileName);
		Reader reader = new InputStreamReader(s);
		return gson.fromJson(reader, Grid.class);
	}
	
	private static void initialize(){
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(Grid.class, new GridLoader());
		gson = builder.serializeNulls().create();
	}

	@Override
	public Grid deserialize(
		JsonElement ele, Type arg1, JsonDeserializationContext arg2
	) throws JsonParseException {		
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
	
	private static Entity loadEntity(JsonElement entityEle){
		String name = entityEle.getAsString();
		
		switch(name.toLowerCase()){
			case "pillar": return new Pillar();
			case "wall": return new Wall();
			case "player": return new Player("Joe");
			default: throw new IllegalArgumentException(
				name + " is an illegal entity"
			);
		}
	}

}
