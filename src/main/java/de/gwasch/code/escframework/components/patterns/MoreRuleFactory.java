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
 * Creates a {@link Rule} for the {@link PatternMatcher} indicated by {@link More}.
 */
public class MoreRuleFactory implements RuleFactory<More> {

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
