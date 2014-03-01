package common.events;

public class KeyEvent extends Event {
    private int keyCode;
    
    
    public KeyEvent(java.awt.event.KeyEvent keyEvent) {
        this(keyEvent.getKeyCode());
    }
    
    public KeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
