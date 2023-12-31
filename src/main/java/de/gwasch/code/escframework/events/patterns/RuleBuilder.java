package de.gwasch.code.escframework.events.patterns;

import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.states.states.State;

/**
 * {@code RuleBuilder} can be used to build a {@code GeneralRule}.
 * It follows the Builder Design Pattern.
 */
public class RuleBuilder {

	private Rule rule;
	
	public RuleBuilder() {
		this(new GeneralRule());
	}
	
	public RuleBuilder(Rule rule) {
		this.rule = rule;
	}
	
	public Rule toRule() {
		return rule;
	}
	
	public RuleBuilder name(String name) {
		rule.setName(name);
		return this;
	}
	
	public RuleBuilder triggerInterval(int triggerInterval) {
		rule.setTriggerInterval(triggerInterval);
		return this;
	}
	
	public RuleBuilder maxTriggerDeviationFactor(double maxTriggerDeviationFactor) {
		rule.setMaxTriggerDeviationFactor(maxTriggerDeviationFactor);
		return this;
	}
	
	public RuleBuilder maxTriggerCount(int maxTriggerCount) {
		rule.setMaxTriggerCount(maxTriggerCount);
		return this;
	}
	
	public RuleBuilder rangeCondition(RangeCondition rangeCondition) {
		rule.setRangeCondition(rangeCondition);
		return this;
	}
	
	public RuleBuilder triggerPatternEvent(Event triggerPatternEvent) {
		rule.getPatternEventControl("trigger").setPatternEvent(triggerPatternEvent);
		return this;
	}
	
	public RuleBuilder consumeTriggerEvents(boolean consumeTriggerEvents) {
		rule.getPatternEventControl("trigger").setConsumeEvents(consumeTriggerEvents);
		return this;
	}
	
	public RuleBuilder eventHistory(EventHistory eventHistory) {
		((TriggerEventControl)rule.getPatternEventControl("trigger")).setEventHistory(eventHistory);
		return this;
	}
	
	public RuleBuilder implicitFirstTrigger(boolean implicitFirstTrigger) {
		rule.setImplicitFirstTrigger(implicitFirstTrigger);
		return this;
	}
	
	public RuleBuilder actionEventState(State<Event> actionEventState) {
		rule.setActionEventState(actionEventState);
		return this;
	}
	
	public RuleBuilder actionEvent(Event actionEvent) {
		rule.setActionEvent(actionEvent);
		return this;
	}
	
	public RuleBuilder actionFinishEvent(Event actionFinishEvent) {
		rule.setActionFinishEvent(actionFinishEvent);
		return this;
	}
	
	public RuleBuilder actionInterval(int actionInterval) {
		rule.setActionInterval(actionInterval);
		return this;
	}
	
	public RuleBuilder maxActionDeviationFactor(double maxActionDeviationFactor) {
		rule.setMaxActionDeviationFactor(maxActionDeviationFactor);
		return this;
	}
	
	public RuleBuilder maxActionCount(int maxActionCount) {
		rule.setMaxActionCount(maxActionCount);
		return this;
	}
	
	public RuleBuilder firstActionImmediately(boolean firstActionImmediately) {
		rule.setFirstActionImmediately(firstActionImmediately);
		return this;
	}
	
	public RuleBuilder activatePatternEventState(State<Event> activatePatternEventState) {
		rule.getPatternEventControl("activate").setPatternEventState(activatePatternEventState);
		return this;
	}
	
	public RuleBuilder activatePatternEvent(Event activatePatternEvent) {
		rule.getPatternEventControl("activate").setPatternEvent(activatePatternEvent);
		return this;
	}
	
	public RuleBuilder consumeActivateEvents(boolean consumeActivateEvents) {
		rule.getPatternEventControl("activate").setConsumeEvents(consumeActivateEvents);
		return this;
	}
	
	public RuleBuilder deactivatePatternEventState(State<Event> deactivatePatternEventState) {
		rule.getPatternEventControl("deactivate").setPatternEventState(deactivatePatternEventState);
		return this;
	}
	
	public RuleBuilder deactivatePatternEvent(Event deactivatePatternEvent) {
		rule.getPatternEventControl("deactivate").setPatternEvent(deactivatePatternEvent);
		return this;
	}
	
	public RuleBuilder consumeDeactivateEvents(boolean consumeDeactivateEvents) {
		rule.getPatternEventControl("deactivate").setConsumeEvents(consumeDeactivateEvents);
		return this;
	}
	public RuleBuilder suspendPatternEventState(State<Event> suspendPatternEventState) {
		rule.getPatternEventControl("suspend").setPatternEventState(suspendPatternEventState);
		return this;
	}
	
	public RuleBuilder suspendPatternEvent(Event suspendPatternEvent) {
		rule.getPatternEventControl("suspend").setPatternEvent(suspendPatternEvent);
		return this;
	}
	
	public RuleBuilder consumeSuspendEvents(boolean consumeSuspendEvents) {
		rule.getPatternEventControl("suspend").setConsumeEvents(consumeSuspendEvents);
		return this;
	}
	
	public RuleBuilder resumePatternEventState(State<Event> resumePatternEventState) {
		rule.getPatternEventControl("resume").setPatternEventState(resumePatternEventState);
		return this;
	}
	
	public RuleBuilder resumePatternEvent(Event resumePatternEvent) {
		rule.getPatternEventControl("resume").setPatternEvent(resumePatternEvent);
		return this;
	}
	
	public RuleBuilder consumeResumeEvents(boolean consumeResumeEvents) {
		rule.getPatternEventControl("resume").setConsumeEvents(consumeResumeEvents);
		return this;
	}
}
