package client.views;

import common.events.ViewUpdateEvent;

public abstract class View {
	public abstract void updateView(ViewUpdateEvent event);
}
