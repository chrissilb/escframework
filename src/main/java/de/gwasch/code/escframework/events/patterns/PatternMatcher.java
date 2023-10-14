package de.gwasch.code.escframework.events.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.events.processors.Dispatcher;
import de.gwasch.code.escframework.events.processors.Processor;

//todo, generieren von abstrakten events anhand von patterns...
/**
 * A {@code PatternMatcher} registers for events at a {@link Dispatcher}. It is parameterized by
 * a set of rules. Every {@link Rule} interprets incoming events and pushes new events into the
 * processor network according to its algorithm.
 */
public class PatternMatcher {

	class ActionFinishEventControl extends EventControl {

		public String getName() {
			return "actionFinish";
		}
		
		public boolean onEvent(Event event) {
			
			if (actionFinishEventRulesMap.containsKey(event)) {
				
				Set<Rule> rules = actionFinishEventRulesMap.get(event);
				
				for (Rule rule : rules) {
					assert rule.getRuleMode() == RuleMode.PROCESSING_ACTION;
					rule.onActionFinishEvent();
				}
			}
			return false;
		}
	}

	class EventHandler extends EventAdapter<Event> {

		public boolean onProcess(Event event) {
						
			try {
//				if (event instanceof InvocationEvent) {
//					InvocationEvent ie = (InvocationEvent)event;
//					if (ie.getMethod().getName().equals("doCountdown")) {
//						System.out.println("hier");
//					}
//				}
				
				boolean consume = false;
				
				if (event instanceof TriggerIntervalEvent) {
					consume = true;
					TriggerIntervalEvent triggerIntervalEvent = (TriggerIntervalEvent)event;
					Rule rule = triggerIntervalEvent.getRule();
					rule.setLastEvent(triggerIntervalEvent);
					rule.onTriggerIntervalEvent();
				}
				else if (event instanceof ActionIntervalEvent) {				
					consume = true;
					ActionIntervalEvent actionIntervalEvent = (ActionIntervalEvent)event;
					Rule rule = actionIntervalEvent.getRule();
					rule.setLastEvent(actionIntervalEvent);
					rule.onActionIntervalEvent();
				}
				else  {
					int controlEventIndex;
					
					for (controlEventIndex = 0; controlEventIndex < controlEventTypes.size(); controlEventIndex++) {
						EventControlType cet = controlEventTypes.get(controlEventIndex);
						for (EventControl ce : cet.getEventControls()) {
							consume |= ce.onEvent(event);
						}
					}
				}
				
				return !consume;
			}
			catch(Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	// Current restrictions: 
	// - One rule references 0 to 1 ranges
	// - One range references 1 rule
	// - Once event can be considered by several ranges
	private Dispatcher<Event> dispatcher;
	private Processor<Event> processor;
	private EventHandler eventHandler;

	
	private List<Rule> rules;
	private List<EventControlType> controlEventTypes;

	private Map<Event, Set<Rule>> actionFinishEventRulesMap;
		
	
	public PatternMatcher(Dispatcher<Event> dispatcher, Processor<Event> processor) {
		this.dispatcher = dispatcher;
		this.processor = processor;
		eventHandler = new EventHandler();

		rules = new ArrayList<>();
		actionFinishEventRulesMap = new HashMap<>();

		EventControlType actionFinishEventControlType = new EventControlType("actionFinish");
		actionFinishEventControlType.addEventControl(new ActionFinishEventControl());
		
		controlEventTypes = new ArrayList<>();
		addControlEventType(new EventControlType("deactivate"));
		addControlEventType(new EventControlType("suspend"));
		addControlEventType(new EventControlType("trigger"));
		addControlEventType(actionFinishEventControlType);
		addControlEventType(new EventControlType("activate"));
		addControlEventType(new EventControlType("resume"));	
	}
	
	public Processor<Event> getProcessor() {
		return processor;
	}
	
	public void addRule(Rule rule) {
		assert rule != null;
		
		if (rules.size() == 0) {
			dispatcher.register(Event.class, eventHandler);
		}
		
		rules.add(rule);
		
		for (EventControl ce : rule.getEventControls()) {
			getControlEventType(ce.getName()).addEventControl(ce);
		}
		
		rule.setPatternMatcher(this);
	}
	
	public void removeRule(Rule rule) {
		
		if (!rules.contains(rule)) {
			return;
		}
		
		if (rule.getRuleMode() != RuleMode.INACTIVE) {
			rule.getInvocationEventControl("deactivate").getRuleEventListener().onEvent();
		}
		
		if (rules.size() == 0) {
			dispatcher.unregister(Event.class, eventHandler);
		}

		for (EventControl ce : rule.getEventControls()) {
			getControlEventType(ce.getName()).removeEventControl(ce);
		}
		
		rules.remove(rule);
	}
	
	public void clearRules() {
		
		if (rules.size() == 0) {
			return;
		}
		
		for (Rule rule : rules) {
			if (rule.getRuleMode() != RuleMode.INACTIVE) {
				rule.getInvocationEventControl("deactivate").getRuleEventListener().onEvent();
			}
		}
		
		dispatcher.unregister(Event.class, eventHandler);
	}
	
	public List<Rule> getRules() {
		return rules;
	}
	
	public Rule getRuleByName(String name) {
		
		for (Rule rule : rules) {
			if (rule.getName() != null && rule.getName().equals(name)) {
				return rule;
			}
		}
		
		return null;
	}
	
	public List<Rule> getRulesByName(String name) {
		
		List<Rule> foundRules = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.getName() != null && rule.getName().equals(name)) {
				foundRules.add(rule);
			}
		}
		
		return foundRules;
	}

	
	public Rule getRuleByActivatePatternEvent(Event activatePatternEvent) {
		
		for (Rule rule : rules) {
			if (rule.getInvocationEventControl("activate").getPatternEvent() != null 
				&& activatePatternEvent.equals(rule.getInvocationEventControl("activate").getPatternEvent())) {
				return rule;
			}
		}
		
		return null;
	}
	
	public List<Rule> getRulesByActivatePatternEvent(Event activatePatternEvent) {
		
		List<Rule> foundRules = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.getInvocationEventControl("activate").getPatternEvent() != null 
				&& activatePatternEvent.equals(rule.getInvocationEventControl("activate").getPatternEvent())) {
				foundRules.add(rule);
			}
		}
		
		return foundRules;
	}

	public void addControlEventType(EventControlType controlEventType) {
		controlEventTypes.add(controlEventType);
	}
	
	public EventControlType getControlEventType(String name) {
		for (EventControlType cet : controlEventTypes) {
			if (cet.getName().equals(name)) {
				return cet;
			}
		}
		
		return null;
	}
		
	public Map<Event, Set<Rule>> getActionFinishEventRulesMap() {
		return actionFinishEventRulesMap;
	}
	
	public void registerActionFinishEvent(Rule rule) {
		
		Event event = rule.getActionFinishEvent();
		
		if (!actionFinishEventRulesMap.containsKey(event)) {
			actionFinishEventRulesMap.put(event, new HashSet<>());
		}
		Set<Rule> rules = actionFinishEventRulesMap.get(event);
		rules.add(rule);		
	}
	
	public void unregisterActionFinishEvent(Rule rule) {

		Event event = rule.getActionFinishEvent();

		Set<Rule> rules = actionFinishEventRulesMap.get(event);
		if (rules != null) {
			if (rules.size() == 1) {
				actionFinishEventRulesMap.remove(event);
			}
			else {
				rules.remove(rule);
			}
		}
	}	
}
