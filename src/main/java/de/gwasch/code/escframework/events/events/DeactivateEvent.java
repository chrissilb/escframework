package de.gwasch.code.escframework.events.events;

/**
 * {@code DeactivateEvent} is a control event to disable process events.
 */
public class DeactivateEvent extends AbstractEvent {
	
	/**
	 * Constructs a {@code DeactivateEvent}.
	 */
	public DeactivateEvent() {	
	}
	
	public DeactivateEvent clone() {
		DeactivateEvent clone = new DeactivateEvent();
		return clone;
	}
}
