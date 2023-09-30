package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.Less;
import de.gwasch.code.escframework.components.events.InvocationEvent;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.CompareCondition;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

/**
 * Creates a {@link Rule} for the {@link PatternMatcher} indicated by {@link Less}.
 */
public class LessRuleFactory implements RuleFactory<Less> {

	public Rule createRule(Less less, Object thiz, Method method) {

		InvocationEvent actionEvent;

		return new RuleBuilder()
			.name(Less.class.getSimpleName().toLowerCase())
			.triggerInterval(less.interval())
			.maxTriggerCount(less.invocations() - 1)
			.triggerCountCompareCondition(CompareCondition.LE_AFTER)
			.triggerPatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
			.actionEvent(actionEvent = InstanceAllocator.createInvocationEvent(thiz, less.methodName()))
			.actionFinishEvent(InstanceAllocator.createReturnEvent(thiz, actionEvent))
			.toRule();
	}
}
