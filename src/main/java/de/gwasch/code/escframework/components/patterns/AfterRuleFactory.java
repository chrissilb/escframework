package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.After;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

/**
 * Creates a {@link Rule} for the {@link PatternMatcher} indicated by {@link After}.
 */
public class AfterRuleFactory implements RuleFactory<After> {

	public Rule createRule(After after, Object thiz, Method method) {
		
		return new RuleBuilder()
			.name(After.class.getSimpleName().toLowerCase())
			.triggerPatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
			.actionInterval(after.interval())
			.actionEvent(InstanceAllocator.createInvocationEvent(thiz, after.methodName()))
			.toRule();
	}
}
