package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;

public class TriggerEventControl extends PatternEventControl {
	
	private int lastEventId;
	private EventHistory eventHistory;			// todo, evtl entfernen, indem man bei activate und deactivate nur neue events akzeptiert
		
	private Range range;
	private boolean registerTriggerEvent;
	
	public TriggerEventControl(Rule rule, String eventName, boolean consumeEvents, RuleEventListener handler) {
		super(rule, eventName, consumeEvents, handler);
		
		lastEventId = -1;
		eventHistory = EventHistory.EVERY;
		
		range = null;
		registerTriggerEvent = false;
	}
		
	public EventHistory getEventHistory() {
		return eventHistory;
	}
	
	public void setEventHistory(EventHistory eventHistory) {
		this.eventHistory = eventHistory;
	}
		
	public Range getRange() {
		return range;
	}
	
	public void setRange(Range range) {
		this.range = range;
	}
	
	public boolean registerTriggerEvent() {
		return registerTriggerEvent;
	}
	
	public void setRegisterTriggerEvent(boolean doRegister) {
		registerTriggerEvent = doRegister;
	}
	
	public boolean onEvent(Event event) {
		
		if (!getRuleEventListener().accept(getRule().getRuleMode()) || getPatternEvent() == null || !getPatternEvent().equals(event)) {
			return false;
		}

		if (eventHistory != EventHistory.EVERY) {
			boolean isNewEvent = event.getId() > lastEventId;
			
			if (isNewEvent) {
				lastEventId = event.getId();
				if (eventHistory == EventHistory.REINSERTED) {
					return false;
				}
			}
			else {
				if (eventHistory == EventHistory.NEW) {
					return false;
				}
			}
		}

		if (range != null) {
			range.add(event);
		}
		if (registerTriggerEvent) {
			setEvent(event);
			getRuleEventListener().onEvent();
		}
	
		return consumeEvents();
	}
}
