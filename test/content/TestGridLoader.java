package content;

import static org.junit.Assert.*;

import java.awt.Dimension;

import org.junit.Test;

import common.models.Grid;

import server.content.GridLoader;

public class TestGridLoader {

	@Test
	public void test() {
		Grid grid = GridLoader.loadGrid("grid1.json");
		assertEquals(grid.getSize(), new Dimension(4, 4));
	}

}
