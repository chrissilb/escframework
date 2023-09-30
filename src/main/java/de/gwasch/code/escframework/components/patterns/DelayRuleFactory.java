package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.Delay;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.EventHistory;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

/**
 * Creates a {@link Rule} for the {@link PatternMatcher} indicated by {@link Delay}.
 */
public class DelayRuleFactory implements RuleFactory<Delay> {

	public Rule createRule(Delay delay, Object thiz, Method method) {
		
		RuleBuilder rb = new RuleBuilder();
		rb.name(Delay.class.getSimpleName().toLowerCase())
		  .triggerPatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
		  .actionEventState(rb.toRule().getInvocationEventControl("trigger").getEventState())
		  .actionInterval(delay.interval())
		  .consumeTriggerEvents(true)
		  .eventHistory(EventHistory.NEW);
		
		return rb.toRule();
	}
}
