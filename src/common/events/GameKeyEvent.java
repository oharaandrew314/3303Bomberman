package common.events;

public class GameKeyEvent implements Event {
    private int keyCode;
    
    
    public GameKeyEvent(java.awt.event.KeyEvent keyEvent) {
        this(keyEvent.getKeyCode());
    }
    
    public GameKeyEvent(int keyCode) {
        this.keyCode = keyCode;
    }

    public int getKeyCode() {
        return keyCode;
    }
}
