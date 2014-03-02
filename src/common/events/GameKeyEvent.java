package common.events;

public class GameKeyEvent extends Event {

	private static final long serialVersionUID = 615019145696546159L;
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
