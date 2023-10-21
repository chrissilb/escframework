package de.gwasch.code.escframework.events.events;

/**
 * {@code ActivateEvent} is a control event to enable process events.
 */
public class ActivateEvent extends AbstractEvent {
	
	/**
	 * Constructs an {@code ActivateEvent}.
	 */
	public ActivateEvent() {	
	}
	
	public ActivateEvent clone() {
		ActivateEvent clone = new ActivateEvent();
		return clone;
	}
}
