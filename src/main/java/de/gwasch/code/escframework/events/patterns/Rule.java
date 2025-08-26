package de.gwasch.code.escframework.events.patterns;

import java.util.ArrayList;
import java.util.List;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.states.states.SimpleState;
import de.gwasch.code.escframework.states.states.State;

/**
 * A {@code Rule} is the main entity of a {@link PatternMatcher}. It contains a
 * group of incoming event patterns and further conditions. Based on this it
 * creates outgoing events.
 */
public abstract class Rule {

	private PatternMatcher patternMatcher;

	private String name;
	private SimpleState<RuleMode> ruleMode;
	private List<EventControl> eventControls; // todo, controlEvents mit gleichem Typ sind möglich

	private int triggerInterval; // Zeitraum in dem Trigger-Events in einer Range gesammelt werden
	private double maxTriggerDeviationFactor;
	private int maxTriggerCount; // Größe der Range
	private RangeCondition rangeCondition;
	private boolean implicitFirstTrigger;

	private State<Event> actionEvent;
	private Event actionFinishEvent;
	private int actionInterval; // Zeitraum nachdem ein Action-Event ausgelöst wird
	private double maxActionDeviationFactor;
	private int maxActionCount; // Anzahl von Action-Events
	private boolean firstActionImmediately;

	private TriggerIntervalEvent triggerIntervalEvent;
	private ActionIntervalEvent actionIntervalEvent;

	private int actionCount;
	private Event lastEvent;

	/**
	 * Creates a new Rule.
	 */
	public Rule() {
		name = null;
		ruleMode = new SimpleState<>(RuleMode.class, "ruleMode");
		ruleMode.setValue(RuleMode.INACTIVE);

		eventControls = new ArrayList<>();

		triggerInterval = 0;
		maxTriggerDeviationFactor = 0.0;
		maxTriggerCount = -1;
		rangeCondition = RangeCondition.ONCE_EQ;
		implicitFirstTrigger = false;

		actionEvent = new SimpleState<>(Event.class, "actionEvent");
		actionFinishEvent = null;
		actionInterval = 0;
		maxActionDeviationFactor = 0.0;
		maxActionCount = -1;
		firstActionImmediately = false;

		triggerIntervalEvent = null;
		actionIntervalEvent = null;

		actionCount = 0;
		lastEvent = null;
	}

	/**
	 * Returns the corresponding pattern matcher.
	 * @return the corresponding pattern matcher
	 */
	public PatternMatcher getPatternMatcher() {
		return patternMatcher;
	}

	void setPatternMatcher(PatternMatcher patternMatcher) {
		this.patternMatcher = patternMatcher;
	}

	public void addPatternEventControl(PatternEventControl patternEventControl) {
		eventControls.add(patternEventControl);
	}

	public PatternEventControl getPatternEventControl(String name) {
		for (EventControl ec : eventControls) {
			if (ec.getTypeName().equals(name)) {
				return (PatternEventControl) ec;
			}
		}

		return null;
	}

	public void removeEventControl(EventControl controlEvent) {
		eventControls.remove(controlEvent);
	}

	public List<EventControl> getEventControls() {
		return eventControls;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SimpleState<RuleMode> getRuleModeState() {
		return ruleMode;
	}

	public RuleMode getRuleMode() {
		return ruleMode.getValue();
	}

	public void setRuleMode(RuleMode ruleMode) {
		this.ruleMode.setValue(ruleMode);
	}

	public int getTriggerInterval() {
		return triggerInterval;
	}

	// todo, auf änderung reagieren
	public void setTriggerInterval(int interval) {
		this.triggerInterval = interval;
	}

	public double getMaxTriggerDeviationFactor() {
		return maxTriggerDeviationFactor;
	}

	public void setMaxTriggerDeviationFactor(double maxTriggerDeviationFactor) {
		this.maxTriggerDeviationFactor = maxTriggerDeviationFactor;
	}

	public int getMaxTriggerCount() {
		return maxTriggerCount;
	}

	public void setMaxTriggerCount(int maxTriggerCount) {
		this.maxTriggerCount = maxTriggerCount;
	}

	public RangeCondition getRangeCondition() {
		return rangeCondition;
	}

	public void setRangeCondition(RangeCondition rangeCondition) {
		this.rangeCondition = rangeCondition;
	}

	public boolean implicitFirstTrigger() {
		return implicitFirstTrigger;
	}

	public void setImplicitFirstTrigger(boolean implicitFirstTrigger) {
		this.implicitFirstTrigger = implicitFirstTrigger;
	}

	public State<Event> getActionEventState() {
		return actionEvent;
	}

	public void setActionEventState(State<Event> actionEventState) {
		actionEvent = actionEventState;
	}

	public Event getActionEvent() {
		return actionEvent.getValue();
	}

	public void setActionEvent(Event actionEvent) {
		assert actionEvent.getPushTime() == 0; // NOTE: cannot be delayed because of InvocationStack.push()
		this.actionEvent.setValue(actionEvent);
	}

	public Event getActionFinishEvent() {
		return actionFinishEvent;
	}

	public void setActionFinishEvent(Event actionFinishEvent) {
		this.actionFinishEvent = actionFinishEvent;
	}

	public int getActionInterval() {
		return actionInterval;
	}

	public void setActionInterval(int actionInterval) {
		this.actionInterval = actionInterval;
	}

	public double getMaxActionDeviationFactor() {
		return maxActionDeviationFactor;
	}

	public void setMaxActionDeviationFactor(double maxActionDeviationFactor) {
		this.maxActionDeviationFactor = maxActionDeviationFactor;
	}

	public int getMaxActionCount() {
		return maxActionCount;
	}

	public void setMaxActionCount(int maxActionCount) {
		this.maxActionCount = maxActionCount;
	}

	public boolean firstActionImmediately() {
		return firstActionImmediately;
	}

	public void setFirstActionImmediately(boolean firstActionImmediately) {
		this.firstActionImmediately = firstActionImmediately;
	}

	public TriggerIntervalEvent getTriggerIntervalEvent() {
		return triggerIntervalEvent;
	}

	public void setTriggerIntervalEvent(TriggerIntervalEvent triggerIntervalEvent) {
		this.triggerIntervalEvent = triggerIntervalEvent;
	}

	public ActionIntervalEvent getActionIntervalEvent() {
		return actionIntervalEvent;
	}

	public void setActionIntervalEvent(ActionIntervalEvent actionIntervalEvent) {
		this.actionIntervalEvent = actionIntervalEvent;
	}

	public int getActionCount() {
		return actionCount;
	}

	public void incrementActionCount() {
		this.actionCount++;
	}

	public void setActionCount(int actionCount) {
		this.actionCount = actionCount;
	}

	public Event getLastEvent() {
		return lastEvent;
	}

	public void setLastEvent(Event lastEvent) {
		this.lastEvent = lastEvent;
	}
	

	public void onTriggerIntervalEvent() {
	}

	public void onActionIntervalEvent() {
	}

	public void onActionFinishEvent() {
	}

	public String toString() {
		return name;
	}
}
