package de.gwasch.code.escframework.components.patterns;

import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.annotations.After;
import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;
import de.gwasch.code.escframework.events.patterns.RuleBuilder;

/**
 * Factory class that creates a {@link After} rule.
 */
public class AfterRuleFactory implements RuleFactory<After> {

	/**
	 * Constructs an {@code AfterRuleFactory}.
	 */
	public AfterRuleFactory() {	
	}
	
	/**
	 * Creates a {@code Rule} for the {@link PatternMatcher} indicated by {@link After}.
	 * 
	 * @param after the annotation
	 * @param thiz the service instance
	 * @param method the annotated method
	 * @return the created {@code Rule} instance
	 */
	public Rule createRule(After after, Object thiz, Method method) {
		
		return new RuleBuilder()
			.name(After.class.getSimpleName().toLowerCase())
			.triggerPatternEvent(InstanceAllocator.createInvocationEvent(thiz, method))
			.actionInterval(after.interval())
			.actionEvent(InstanceAllocator.createInvocationEvent(thiz, after.methodName()))
			.toRule();
	}
}
