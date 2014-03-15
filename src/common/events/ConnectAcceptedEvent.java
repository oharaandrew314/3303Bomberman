package common.events;

public class ConnectAcceptedEvent extends Event {
	private static final long serialVersionUID = -1622458214395758137L;
	private int assignedPlayerId;
	
	public ConnectAcceptedEvent(){
		assignedPlayerId = -1;
	}
	public ConnectAcceptedEvent(int assignedPlayerId){
		this.assignedPlayerId = assignedPlayerId;
	}
	
	public int getAssignedPlayerId(){
		return assignedPlayerId;
	}
}
