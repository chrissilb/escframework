package de.gwasch.code.escframework.events.patterns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.listeners.EventAdapter;
import de.gwasch.code.escframework.events.processors.Dispatcher;
import de.gwasch.code.escframework.events.processors.Processor;

//todo, generieren von abstrakten events anhand von patterns...

/**
 * A {@code PatternMatcher} registers for events at a {@link Dispatcher}. It is
 * parameterized by a set of rules. Every {@link Rule} interprets incoming
 * events and pushes new events into the processor network according to its
 * algorithm.
 * <p>
 * Incoming events are trigger events and control events. Trigger events might
 * be buffered in a {@link Range}. Its evaluation leads to outgoing events,
 * called action events. Control events are meant to influence the
 * {@link RuleMode} of a{@code Rule}.
 * <p>
 * Incoming events can be distributed to several {@code Rules}. A
 * {@code PatternMatcher} distributes incoming events in the order of a sequence
 * of {@link EventControlType}s. The default order is:
 * <ol>
 * <li>deactivate</li>
 * <li>suspend</li>
 * <li>trigger</li>
 * <li>actionFinish</li>
 * <li>activate</li>
 * <li>resume</li>
 * </ol>
 * A {@code Rule} registers corresponding {@link EventControl}s as shown in
 * {@link GeneralRule#GeneralRule()}.
 * 
 */
public class PatternMatcher {

	private class ActionFinishEventControl implements EventControl {

		public String getTypeName() {
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

	private class EventHandler extends EventAdapter<Event> {

		public boolean onProcess(Event event) {

			boolean consumed = false;
			Throwable throwable = null;
			
			try {
				if (event instanceof TriggerIntervalEvent) {
					consumed = true;
					TriggerIntervalEvent triggerIntervalEvent = (TriggerIntervalEvent) event;
					Rule rule = triggerIntervalEvent.getRule();
					rule.setLastEvent(triggerIntervalEvent);
					rule.onTriggerIntervalEvent();
				} else if (event instanceof ActionIntervalEvent) {
					consumed = true;
					ActionIntervalEvent actionIntervalEvent = (ActionIntervalEvent) event;
					Rule rule = actionIntervalEvent.getRule();
					rule.setLastEvent(actionIntervalEvent);
					rule.onActionIntervalEvent();
				} else {
					int controlEventIndex;

					for (controlEventIndex = 0; controlEventIndex < eventControlTypes.size(); controlEventIndex++) {
						EventControlType cet = eventControlTypes.get(controlEventIndex);
						for (EventControl ce : cet.getEventControls()) {
							consumed |= ce.onEvent(event);
						}
					}
				}

//				return !consume;
			} catch (Throwable t) {
//				throw new RuntimeException(e);
				throwable = t;
			}
			
//			if (consumed && event instanceof InvocationEvent) {
//				System.out.println("hier");
//			}
			
//			return !consumed;
			return postEventHandler.onProcess(PatternMatcher.this, event, consumed, null, throwable);
		}
	}
	
	private class DefaultPostEventHandler implements PostEventListener {
		
		public boolean onProcess(Object source, Event event, boolean consumed, Object returnValue, Throwable throwable) {
			if (throwable != null) {
				throw new RuntimeException(throwable);
			}
			return !consumed;
		}
	}
	
	private class DefaultActionnManager implements ActionManager {

		public Object invoke(Object service, Event event) {
			processor.process(event);
			return null;
		}
		
	}

	// Current restrictions:
	// - One rule references 0 to 1 ranges
	// - One range references 1 rule
	// - Once event can be considered by several ranges
	private Dispatcher<Event> dispatcher;
	private Processor<Event> processor;
	private EventHandler eventHandler;
	
	private PostEventListener postEventHandler;
	private ActionManager actionManager;
	
	private List<Rule> rules;
	private List<EventControlType> eventControlTypes;

	private Map<Event, Set<Rule>> actionFinishEventRulesMap;

	public PatternMatcher(Dispatcher<Event> dispatcher, Processor<Event> processor) {
		this.dispatcher = dispatcher;
		this.processor = processor;
		eventHandler = new EventHandler();
		postEventHandler = new DefaultPostEventHandler();
		actionManager = new DefaultActionnManager(); 

		rules = new ArrayList<>();
		actionFinishEventRulesMap = new HashMap<>();

		EventControlType actionFinishEventControlType = new EventControlType("actionFinish");
		actionFinishEventControlType.addEventControl(new ActionFinishEventControl());

		eventControlTypes = new ArrayList<>();
		addEventControlType(new EventControlType("deactivate"));
		addEventControlType(new EventControlType("suspend"));
		addEventControlType(new EventControlType("trigger"));
		addEventControlType(actionFinishEventControlType);
		addEventControlType(new EventControlType("activate"));
		addEventControlType(new EventControlType("resume"));
	}

	public Dispatcher<Event> getDispatcher() {
		return dispatcher;
	}

	public Processor<Event> getProcessor() {
		return processor;
	}
	
	public void setPostEventListener(PostEventListener listener) {
		postEventHandler = listener;
	}
	
	public ActionManager getActionManager() {
		return actionManager;	
	}
	
	public void setActionManager(ActionManager actionManager) {
		this.actionManager = actionManager;
	}

	public void addRule(Rule rule) {
		assert rule != null;

		if (rules.size() == 0) {
			dispatcher.register(Event.class, eventHandler);
		}

		rules.add(rule);
		
		for (EventControl ce : rule.getEventControls()) {
			getEventControlType(ce.getTypeName()).addEventControl(ce);
		}

		rule.setPatternMatcher(this);		
	}

	public void removeRule(Rule rule) {

		if (!rules.contains(rule)) {
			return;
		}

		if (rule.getRuleMode() != RuleMode.INACTIVE) {
			rule.getPatternEventControl("deactivate").getRuleEventListener().onEvent();
		}

		if (rules.size() == 0) {
			dispatcher.unregister(Event.class, eventHandler);
		}

		for (EventControl ce : rule.getEventControls()) {
			getEventControlType(ce.getTypeName()).removeEventControl(ce);
		}

		rules.remove(rule);
	}

	public void clearRules() {

		if (rules.size() == 0) {
			return;
		}

		for (Rule rule : rules) {
			if (rule.getRuleMode() != RuleMode.INACTIVE) {
				rule.getPatternEventControl("deactivate").getRuleEventListener().onEvent();
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
			if (rule.getPatternEventControl("activate").getPatternEvent() != null
					&& activatePatternEvent.equals(rule.getPatternEventControl("activate").getPatternEvent())) {
				return rule;
			}
		}

		return null;
	}

	public List<Rule> getRulesByActivatePatternEvent(Event activatePatternEvent) {

		List<Rule> foundRules = new ArrayList<>();
		for (Rule rule : rules) {
			if (rule.getPatternEventControl("activate").getPatternEvent() != null
					&& activatePatternEvent.equals(rule.getPatternEventControl("activate").getPatternEvent())) {
				foundRules.add(rule);
			}
		}

		return foundRules;
	}

	public void addEventControlType(EventControlType eventControlType) {
		eventControlTypes.add(eventControlType);
	}

	public void addEventControlType(int index, EventControlType eventControlType) {
		eventControlTypes.add(index, eventControlType);
	}

	public EventControlType getEventControlType(String name) {
		for (EventControlType ect : eventControlTypes) {
			if (ect.getName().equals(name)) {
				return ect;
			}
		}

		return null;
	}

	public void clearEventControlTyppes() {
		eventControlTypes.clear();
	}

	public void removeEventControlType(EventControlType eventControlType) {
		eventControlTypes.remove(eventControlType);
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
			} else {
				rules.remove(rule);
			}
		}
	}
}
