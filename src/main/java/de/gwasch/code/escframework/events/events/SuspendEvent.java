package de.gwasch.code.escframework.events.events;

public class SuspendEvent extends AbstractEvent {
	
	private Event patternEvent;
	
	public SuspendEvent(Event patternEvent) {
		this.patternEvent = patternEvent;
	}
	
	public SuspendEvent() {
		this(null);
	}
	
	public SuspendEvent clone() {
		SuspendEvent clone = new SuspendEvent(patternEvent);
		return clone;
	}
	
	public Event getPatternEvent() {
		return patternEvent;
	}
}
