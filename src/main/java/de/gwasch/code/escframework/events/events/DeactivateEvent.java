package de.gwasch.code.escframework.events.events;

public class DeactivateEvent extends AbstractEvent {

	public DeactivateEvent clone() {
		DeactivateEvent clone = new DeactivateEvent();
		return clone;
	}
}
