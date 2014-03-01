package events;

import common.events.KeyEvent;
import java.awt.Component;
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
            KeyEvent keyEvent = new KeyEvent(42);
            
            //Serialize
            ByteArrayOutputStream serializerOutput = new ByteArrayOutputStream();
            ObjectOutputStream serializer = new ObjectOutputStream(serializerOutput);
            serializer.writeObject(keyEvent);
            serializer.flush();
            byte[] buffer = serializerOutput.toByteArray();
            
            //De-serialize
            ByteArrayInputStream baos = new ByteArrayInputStream(buffer);
            ObjectInputStream deserializer = new ObjectInputStream(baos);
            keyEvent = (KeyEvent)deserializer.readObject();
            
            //Ensure the resulting keycode has not changed.
            assertEquals(42, keyEvent.getKeyCode());
        } catch (IOException ex) {
            Logger.getLogger(TestSerializableKeyEvent.class.getName()).log(Level.SEVERE, null, ex);
            assertTrue("Failed to serialize / deserialize key event", false);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(TestSerializableKeyEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
