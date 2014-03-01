package common.events;

import java.awt.Component;
import java.awt.event.KeyEvent;

public class SerializableKeyEvent extends Event {

    private Component source;
    private int id;
    private long when;
    private int modifiers;
    private int keyCode;
    
    
    public SerializableKeyEvent(KeyEvent keyEvent) {
        this.source = (Component)keyEvent.getSource();
        this.id = keyEvent.getID();
        this.when = keyEvent.getWhen();
        this.modifiers = keyEvent.getModifiers();
        this.keyCode = keyEvent.getKeyCode();
    }
    
    public KeyEvent toKeyEvent() {
        return new KeyEvent(source, id, when, modifiers, keyCode);
    }
}
