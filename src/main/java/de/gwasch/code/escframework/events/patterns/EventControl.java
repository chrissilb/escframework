package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

public abstract class EventControl {

	public EventControl() {
	}
	
	public abstract String getName();
	public abstract boolean onEvent(Event event);
}