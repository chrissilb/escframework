package de.gwasch.code.escframework.events.events;

public class ResumeEvent extends AbstractEvent {
	
	private Event patternEvent;
	
	public ResumeEvent(Event patternEvent) {
		this.patternEvent = patternEvent;
	}
	
	public ResumeEvent() {
		this(null);
	}
	
	public ResumeEvent clone() {
		ResumeEvent clone = new ResumeEvent(patternEvent);
		return clone;
	}
	
	public Event getPatternEvent() {
		return patternEvent;
	}
}
