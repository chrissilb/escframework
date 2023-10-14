package de.gwasch.code.escframework.components.patterns;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;

/**
 * Common interface for rule factories.
 *  
 * @param <T> the annotation type of the concrete {@code RuleFactory}
 * 
 * @see PatternMatcher
 */
public interface RuleFactory <T extends Annotation> {
	Rule createRule(T annotation, Object thiz, Method method);
}
