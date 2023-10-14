package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.More;
import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.CompareCondition;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

/**
 * Factory class that creates a {@link More} rule.
 */
public class MoreRuleFactory implements RuleFactory<More> {

	/**
	 * Creates a {@code Rule} for the {@link PatternMatcher} indicated by {@link More}.
	 * 
	 * @param more the annotation
	 * @param thiz the service instance
	 * @param method the annotated method
	 * @return the created {@code Rule} instance
	 */
	public Rule createRule(More more, Object thiz, Method method) {
		
		InvocationEvent actionEvent;

		return new RuleBuilder()
			.name(More.class.getSimpleName().toLowerCase())
			.triggerInterval(more.interval())
			.maxTriggerCount(more.invocations())
			.triggerCountCompareCondition(CompareCondition.ONCE_MT)
			.triggerPatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
			.actionEvent(actionEvent = InstanceAllocator.createInvocationEvent(thiz, more.methodName()))
			.actionFinishEvent(InstanceAllocator.createReturnEvent(thiz, actionEvent))
			.toRule();
	}
}
