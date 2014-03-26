package common.events;

public class DisconnectEvent extends Event {
	private static final long serialVersionUID = 6836643438949521148L;
	
	public final int disconnectedPlayerId;
	
	public DisconnectEvent(){
		disconnectedPlayerId = -1;
	}
	
	public DisconnectEvent(int disconnectedPlayerId){
		this.disconnectedPlayerId = disconnectedPlayerId;
	}
}
