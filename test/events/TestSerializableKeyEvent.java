package events;

import common.events.Event;
import common.events.SerializableKeyEvent;
import java.awt.Component;
import java.awt.event.KeyEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestSerializableKeyEvent extends Component {
    @Test
    public void anAwtKeyEventCanBeRestoredAfterSerialization() {
        try {
            //Create a serializableKeyEvent based off an Awt KeyEvent
            KeyEvent originalKeyEvent = new KeyEvent(this, 1, 2, 3, 4);
            SerializableKeyEvent serializableKeyEvent = new SerializableKeyEvent(originalKeyEvent);
            
            //Serialize
            ByteArrayOutputStream serializerOutput = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializerOutput);
            serializer.writeObject(serializableKeyEvent);
            serializer.flush();
            byte[] buffer = serializerOutput.toByteArray();
            
            //De-serialize
            ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
            ObjectInputStream deserializer = new ObjectInputStream(baos);
            serializableKeyEvent = (SerializableKeyEvent)deserializer.readObject();
            KeyEvent processedKeyEvent = serializableKeyEvent.toKeyEvent();
            
            //Ensure the resulting Awt KeyEvent is the same as the original.
            assertTrue(processedKeyEvent.getSource() instanceof TestSerializableKeyEvent);
            assertEquals(originalKeyEvent.getID(), processedKeyEvent.getID());
            assertEquals(originalKeyEvent.getWhen(), processedKeyEvent.getWhen());
            assertEquals(originalKeyEvent.getModifiers(), processedKeyEvent.getModifiers());
            assertEquals(originalKeyEvent.getKeyCode(), processedKeyEvent.getKeyCode());
        } catch (IOException ex) {
            Logger.getLogger(TestSerializableKeyEvent.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue("Failed to serialize / deserialize key event", false);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TestSerializableKeyEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
