package content;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import common.models.Door;
import common.models.Entity;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.Wall;
import server.content.GridLoader;

public class TestGridLoader {
	
	private Grid grid;
	private final Set<Entity> checked = new HashSet<>();
	
	@Before
	public void setUp(){
		checked.clear();
	}

	@Test
	public void test() {
		grid = GridLoader.loadGrid("grid1.json");
		assertEquals(grid.getSize(), new Dimension(4, 4));
		
		assertEntity(1, 1, Pillar.class);
		assertEntity(1, 2, Wall.class);
		assertEntity(1, 2, Door.class);
		assertPlayer(2, 2, "Player 1");
		assertPlayer(3, 2, "Player 2");
		assertEntity(3, 3, Pillar.class);
		assertRemaining();
	}
	
	private void assertPlayer(int x, int y, String name){
		Player p = (Player) assertEntity(x, y, Player.class);
		assertEquals(p.name, name);
	}
	
	private Entity assertEntity(int x, int y, Class<? extends Entity> type){
		Entity entity = getEntity(x, y, type);
		assertNotNull(entity);
		checked.add(entity);
		return entity;
	}
	
	private Entity getEntity(int x, int y, Class<? extends Entity> type){
		for (Entity entity : grid.get(new Point(x, y))){
			if(entity.getClass().equals(type)){
				return entity;
			}
		}
		return null;
	}
	
	private void assertRemaining(){
		for (Point point : grid.keySet()){
			for (Entity entity : grid.get(point)){
				assertTrue(
					"Extra entity, " + entity.name + " was found on the grid.",
					checked.contains(entity)
				);
			}
		}
	}

}
