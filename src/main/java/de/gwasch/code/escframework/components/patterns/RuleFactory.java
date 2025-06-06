package de.gwasch.code.escframework.components.patterns;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import de.gwasch.code.escframework.components.utils.InstanceAllocator;
import de.gwasch.code.escframework.events.patterns.PatternMatcher;
import de.gwasch.code.escframework.events.patterns.Rule;

/**
 * Common interface for rule factories.
 *  
 * @param <T> the annotation type of the concrete {@code RuleFactory}
 * 
 * @see InstanceAllocator
 * @see PatternMatcher
 */
public interface RuleFactory <T extends Annotation> {
	
	/**
	 * Creates a new {@code Rule}.
	 * @param annotation the {@link de.gwasch.code.escframework.components.annotations.Rule} annotation
	 * @param thiz the component instance
	 * @param method the annotated method
	 * @return the new {@code Rule}
	 */
	Rule createRule(T annotation, Object thiz, Method method);
}
