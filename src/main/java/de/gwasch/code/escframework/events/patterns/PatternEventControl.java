package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.states.states.SimpleState;
import de.gwasch.code.escframework.states.states.State;

public abstract class PatternEventControl implements EventControl {

	private Rule rule;
	private State<Event> patternEvent;
	private boolean consumeEvents;
	private SimpleState<Event> event;
	private RuleEventListener handler;
	
	protected PatternEventControl(Rule rule, String typeName, boolean consumeEvents, RuleEventListener handler) {
		this.rule = rule;
		patternEvent = new SimpleState<>(Event.class, typeName + "Pattern");
		this.consumeEvents = consumeEvents;
		event = new SimpleState<>(Event.class, typeName);
		this.handler = handler;
	}
	
	
	public Rule getRule() {
		return rule;
	}
	
	public String getTypeName() {
		return getEventState().getName();
	}
	public State<Event> getPatternEventState() {
		return patternEvent;
	}

	public void setPatternEventState(State<Event> patternEventState) {
		patternEvent = patternEventState;
	}
	
	public Event getPatternEvent() {
		return patternEvent.getValue();
	}
	
	public void setPatternEvent(Event patternEvent) {
		this.patternEvent.setValue(patternEvent);
	}
	
	public boolean consumeEvents() {
		return consumeEvents;
	}
	
	public void setConsumeEvents(boolean consumeEvents) {
		this.consumeEvents = consumeEvents;
	}
	
	public SimpleState<Event> getEventState() {
		return event;
	}
	
	public Event getEvent() {
		return event.getValue();
	}
	
	public void setEvent(Event event) {
		getRule().setLastEvent(event);
		this.event.setValue(event);
	}
	
	public RuleEventListener getRuleEventListener() {
		return handler;
	}
		
	public String toString() {
		return patternEvent.toString();
	}
}