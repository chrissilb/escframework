package de.gwasch.code.escframework.events.events;

/**
 * {@code CancelEvent} is a control event to cancels process events.
 */
public class CancelEvent extends AbstractEvent {
	
	private Event patternEvent;
	
	public CancelEvent(Event patternEvent) {
		this.patternEvent = patternEvent;
	}

	public CancelEvent() {
		this(null);
	}
	
	public CancelEvent clone() {
		CancelEvent clone = new CancelEvent(patternEvent);
		return clone;
	}
	
	public Event getPatternEvent() {
		return patternEvent;
	}
}
