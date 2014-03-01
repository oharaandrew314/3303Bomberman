package events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Before;
import org.junit.Test;

import common.events.ViewUpdateEvent;
import common.models.Door;
import common.models.Grid;
import common.models.Pillar;
import common.models.Player;
import common.models.Wall;

public class TestViewUpdateEvent {
    private ViewUpdateEvent viewUpdateEvent;
    
    @Before
    public void createViewUpdateEvent() {
        Grid grid = new Grid(new Dimension(3, 3));
        grid.set(new Wall(), new Point(2, 0));	
        grid.set(new Pillar(), new Point(0, 1));
        grid.set(new Player("Peter"), new Point(1, 1));
        grid.set(new Door(), new Point(2, 2));
        
        viewUpdateEvent = new ViewUpdateEvent(grid);
    }
    
    @Test
    public void aGridCanBeRestoredAfterSerialization() {
        try {        
            //Grab the original grid
            Grid originalGrid = viewUpdateEvent.getGrid();
            
            //Serialize
            ByteArrayOutputStream serializerOutput = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializerOutput);
            serializer.writeObject(viewUpdateEvent);
            serializer.flush();
            byte[] buffer = serializerOutput.toByteArray();

            //De-serialize
            ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
            ObjectInputStream deserializer = new ObjectInputStream(baos);
            viewUpdateEvent = (ViewUpdateEvent)deserializer.readObject();
            
            // Assert no changes to the grid.
            Grid processedGrid = viewUpdateEvent.getGrid();
            assertEquals(originalGrid.toString(), processedGrid.toString());
            
        } catch (IOException ex) {
            Logger.getLogger(TestSerializableKeyEvent.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue("Failed to serialize / deserialize viewUpdateEvent", false);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TestSerializableKeyEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
