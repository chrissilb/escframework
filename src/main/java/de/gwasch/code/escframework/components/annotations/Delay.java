package de.gwasch.code.escframework.components.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated method is called after a certain time span.
 * <p>
 * The annotated method must have to following signature:
 * {@code public void <<methodName>>()}.
 * 
 * @see de.gwasch.code.escframework.events.patterns.Rule
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(DelayList.class)
@Rule
public @interface Delay {
	
	/**
	 * Returns the time interval to delay the method invocation.
	 * 
	 * @return the time interval in milliseconds
	 */
	int interval();
}
