package common.events;

public class GameKeyEventAck extends GameKeyEvent {
	
	private static final long serialVersionUID = 4098456853732000736L;

	public GameKeyEventAck(GameKeyEvent event){
		super(event.getKeyCode());
	}

}
