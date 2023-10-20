package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.Within;
import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.events.Event;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;
import de.gwasch.code.escframework.states.states.Function;
import de.gwasch.code.escframework.states.states.FunctionState;

/**
 * Factory class that creates a {@link Within} rule.
 */
public class WithinRuleFactory implements RuleFactory<Within> {

	/**
	 * Creates a {@code Rule} for the {@link PatternMatcher} indicated by {@link Within}.
	 * 
	 * @param within the annotation
	 * @param thiz the service instance
	 * @param method the annotated method
	 * @return the created {@code Rule} instance
	 */
	public Rule createRule(Within within, Object thiz, Method method) {
		
		RuleBuilder rb = new RuleBuilder();
		final Rule rule = rb.toRule();

		Function<Event> deactivateFunction = new Function<>() {
			public Event getValue() {
				if (rule.getPatternEventControl("activate").getEvent() == null) {
					return null;
				}
				return InstanceAllocator.createReturnEvent(thiz, (InvocationEvent)rule.getPatternEventControl("activate").getEvent());			
			}				
		};
		
		FunctionState<Event> deactivateFunctionState = new FunctionState<>(Event.class, "deactivatePattern");
		deactivateFunctionState.addParamState(rule.getPatternEventControl("activate").getEventState());
		deactivateFunctionState.setFunction(deactivateFunction);
		
		rb.name(Within.class.getSimpleName().toLowerCase())
		  .implicitFirstTrigger(true)
		  .actionInterval(within.interval())
		  .actionEvent(InstanceAllocator.createInvocationEvent(thiz, within.methodName()))
		  .activatePatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
		  .consumeActivateEvents(false)
		  .deactivatePatternEventState(deactivateFunctionState)
		  .consumeDeactivateEvents(false);
		
		return rule;
	}
}
