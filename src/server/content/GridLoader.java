package server.content;

import java.awt.Dimension;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import common.models.Grid;

public class GridLoader implements JsonSerializer<Grid>, JsonDeserializer<Grid>{
	
	private static final String GRID_PATH = "grids.";
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
	public JsonElement serialize(
		Grid grid, Type arg1, JsonSerializationContext arg2
	) {
		JsonObject sizeObj = new JsonObject();
		sizeObj.addProperty("width", grid.getSize().width);
		sizeObj.addProperty("height", grid.getSize().height);
		
		JsonObject gridObj = new JsonObject();
		gridObj.add("size", sizeObj);
		
		return gridObj;
	}

	@Override
	public Grid deserialize(
		JsonElement ele, Type arg1, JsonDeserializationContext arg2
	) throws JsonParseException {
		//JsonObject gridObj = new JsonObject();
		
		JsonObject gridObj = ele.getAsJsonObject();
		
		JsonObject sizeObj = gridObj.get("size").getAsJsonObject();
		
		Grid grid = new Grid(
			new Dimension(
				sizeObj.get("width").getAsInt(),
				sizeObj.get("height").getAsInt()
			)
		);
		
		return grid;
	}

}
