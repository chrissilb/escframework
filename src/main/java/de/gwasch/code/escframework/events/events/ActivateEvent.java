package de.gwasch.code.escframework.events.events;

public class ActivateEvent extends AbstractEvent {
	
	public ActivateEvent clone() {
		ActivateEvent clone = new ActivateEvent();
		return clone;
	}
}
