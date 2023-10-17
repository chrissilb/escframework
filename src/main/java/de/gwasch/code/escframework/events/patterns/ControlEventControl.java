package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

public class ControlEventControl extends PatternEventControl {

	public ControlEventControl(Rule rule, String typeName, boolean consumeEvents, RuleEventListener handler) {
		super(rule, typeName, consumeEvents, handler);

	}

	public boolean onEvent(Event event) {	
		
		if (getPatternEvent() == null || !getPatternEvent().equals(event)) {
			return false;
		}
		
		if (getRuleEventListener().accept(getRule().getRuleMode())) {
			setEvent(event);
			getRuleEventListener().onEvent();
		}
		
		//NOTE: consumeEvents() must also be considered if event is not processed further.
		return consumeEvents();
	}
}
