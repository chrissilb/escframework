package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.Less;
import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.RangeCondition;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

/**
 * Factory class that creates a {@link Less} rule.
 */
public class LessRuleFactory implements RuleFactory<Less> {

	/**
	 * Constructs a {@code LessRuleFactory}.
	 */
	public LessRuleFactory() {	
	}
	
	/**
	 * Creates a {@code Rule} for the {@link PatternMatcher} indicated by {@link Less}.
	 * 
	 * @param less the annotation
	 * @param thiz the service instance
	 * @param method the annotated method
	 * @return the created {@code Rule} instance
	 */
	public Rule createRule(Less less, Object thiz, Method method) {

		InvocationEvent actionEvent;

		return new RuleBuilder()
			.name(Less.class.getSimpleName().toLowerCase())
			.triggerInterval(less.interval())
			.maxTriggerCount(less.invocations() - 1)
			.rangeCondition(RangeCondition.LE_AFTER)
			.triggerPatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
			.actionEvent(actionEvent = InstanceAllocator.createInvocationEvent(thiz, less.methodName()))
			.actionFinishEvent(InstanceAllocator.createReturnEvent(thiz, actionEvent))
			.toRule();
	}
}
