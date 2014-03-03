package test.integration.helpers;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

import common.models.Entity;
import common.models.Grid;
import common.models.Player;

public class IntegrationHelper {

	public static List<Player> findPlayers(Grid grid, int numPlayers){
		List<Player> players = new ArrayList<>();
		for (Point point : grid.keySet()){
			for (Entity entity: grid.get(point)){
				if (entity instanceof Player){
					players.add((Player) entity);
				}
			}
		}
		assertEquals(numPlayers, players.size());
		return players;
	} 

}
